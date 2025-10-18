package com.msncakma.msnairdrop.database;

import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
    private final MsnAirDrop plugin;
    private final DatabaseConnector connector;

    // SQL Queries
    private static final String CREATE_EVENTS_TABLE = """
        CREATE TABLE IF NOT EXISTS msnairdrop_events (
            id INTEGER PRIMARY KEY AUTO_INCREMENT,
            event_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            world VARCHAR(64),
            x INT,
            y INT,
            z INT,
            winner_uuid VARCHAR(36),
            winner_name VARCHAR(32),
            reward_name VARCHAR(64),
            test_mode BOOLEAN DEFAULT FALSE
        )
    """;

    private static final String CREATE_STATS_TABLE = """
        CREATE TABLE IF NOT EXISTS msnairdrop_stats (
            player_uuid VARCHAR(36) PRIMARY KEY,
            player_name VARCHAR(32),
            wins INT DEFAULT 0,
            participations INT DEFAULT 0,
            last_win TIMESTAMP,
            total_rewards INT DEFAULT 0
        )
    """;

    private static final String CREATE_REWARDS_LOG = """
        CREATE TABLE IF NOT EXISTS msnairdrop_rewards_log (
            id INTEGER PRIMARY KEY AUTO_INCREMENT,
            reward_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            player_uuid VARCHAR(36),
            player_name VARCHAR(32),
            reward_name VARCHAR(64),
            event_id INT,
            FOREIGN KEY (event_id) REFERENCES msnairdrop_events(id)
        )
    """;

    public DatabaseManager(MsnAirDrop plugin) {
        this.plugin = plugin;
        
        // Initialize database connector based on config
        String dbType = plugin.getConfig().getString("database.type", "SQLITE").toUpperCase();
        if (dbType.equals("MYSQL")) {
            String host = plugin.getConfig().getString("database.mysql.host", "localhost");
            int port = plugin.getConfig().getInt("database.mysql.port", 3306);
            String database = plugin.getConfig().getString("database.mysql.database", "msnairdrop");
            String username = plugin.getConfig().getString("database.mysql.username", "root");
            String password = plugin.getConfig().getString("database.mysql.password", "");
            
            this.connector = new MySQLConnector(host, port, database, username, password);
        } else {
            this.connector = new SQLiteConnector(plugin);
        }

        // Initialize database
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = connector.getConnection();
             PreparedStatement eventsStmt = conn.prepareStatement(CREATE_EVENTS_TABLE);
             PreparedStatement statsStmt = conn.prepareStatement(CREATE_STATS_TABLE);
             PreparedStatement rewardsStmt = conn.prepareStatement(CREATE_REWARDS_LOG)) {
            
            eventsStmt.executeUpdate();
            statsStmt.executeUpdate();
            rewardsStmt.executeUpdate();
            
            plugin.getLogger().info("Database tables initialized successfully!");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to initialize database tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void logEvent(Location loc, Player winner, String rewardName, boolean testMode) {
        String query = """
            INSERT INTO msnairdrop_events (world, x, y, z, winner_uuid, winner_name, reward_name, test_mode)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, loc.getWorld().getName());
            stmt.setInt(2, loc.getBlockX());
            stmt.setInt(3, loc.getBlockY());
            stmt.setInt(4, loc.getBlockZ());
            stmt.setString(5, winner.getUniqueId().toString());
            stmt.setString(6, winner.getName());
            stmt.setString(7, rewardName);
            stmt.setBoolean(8, testMode);
            
            stmt.executeUpdate();
            
            // Update player stats
            updatePlayerStats(winner);
            
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to log event: " + e.getMessage());
        }
    }

    public void updatePlayerStats(Player player) {
        String query = """
            INSERT INTO msnairdrop_stats (player_uuid, player_name, wins, last_win)
            VALUES (?, ?, 1, CURRENT_TIMESTAMP)
            ON DUPLICATE KEY UPDATE
            wins = wins + 1,
            player_name = ?,
            last_win = CURRENT_TIMESTAMP
        """;

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setString(3, player.getName());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to update player stats: " + e.getMessage());
        }
    }

    public void logReward(Player player, String rewardName, int eventId) {
        String query = """
            INSERT INTO msnairdrop_rewards_log (player_uuid, player_name, reward_name, event_id)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setString(3, rewardName);
            stmt.setInt(4, eventId);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to log reward: " + e.getMessage());
        }
    }

    public List<EventRecord> getRecentEvents(int limit) {
        List<EventRecord> events = new ArrayList<>();
        String query = """
            SELECT * FROM msnairdrop_events
            ORDER BY event_time DESC
            LIMIT ?
        """;

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                events.add(new EventRecord(
                    rs.getInt("id"),
                    rs.getTimestamp("event_time"),
                    rs.getString("world"),
                    rs.getInt("x"),
                    rs.getInt("y"),
                    rs.getInt("z"),
                    UUID.fromString(rs.getString("winner_uuid")),
                    rs.getString("winner_name"),
                    rs.getString("reward_name"),
                    rs.getBoolean("test_mode")
                ));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get recent events: " + e.getMessage());
        }
        
        return events;
    }

    public PlayerStats getPlayerStats(UUID playerUuid) {
        String query = "SELECT * FROM msnairdrop_stats WHERE player_uuid = ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new PlayerStats(
                    playerUuid,
                    rs.getString("player_name"),
                    rs.getInt("wins"),
                    rs.getInt("participations"),
                    rs.getTimestamp("last_win"),
                    rs.getInt("total_rewards")
                );
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get player stats: " + e.getMessage());
        }
        
        return null;
    }

    public void close() {
        if (connector != null) {
            connector.close();
        }
    }
}