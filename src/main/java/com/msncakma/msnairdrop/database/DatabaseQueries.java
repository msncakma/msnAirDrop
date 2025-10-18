package com.msncakma.msnairdrop.database;

import com.msncakma.msnairdrop.MsnAirDrop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseQueries {
    private final DatabaseConnector connector;
    private final MsnAirDrop plugin;

    public DatabaseQueries(DatabaseConnector connector, MsnAirDrop plugin) {
        this.connector = connector;
        this.plugin = plugin;
    }

    public void recordEvent(String world, int x, int y, int z, UUID winnerUuid, 
                          String winnerName, String rewardName, boolean testMode) {
        String sql = "INSERT INTO event_records (world, x, y, z, winner_uuid, winner_name, reward_name, test_mode) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, world);
            stmt.setInt(2, x);
            stmt.setInt(3, y);
            stmt.setInt(4, z);
            stmt.setString(5, winnerUuid.toString());
            stmt.setString(6, winnerName);
            stmt.setString(7, rewardName);
            stmt.setBoolean(8, testMode);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to record event", e);
        }
    }

    public void updatePlayerStats(UUID playerUuid, String playerName, boolean won) {
        String sql = "INSERT INTO player_stats (player_uuid, player_name, events_won, events_participated, total_rewards) " +
                    "VALUES (?, ?, ?, 1, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "player_name = ?, " +
                    "events_won = events_won + ?, " +
                    "events_participated = events_participated + 1, " +
                    "total_rewards = total_rewards + ?";

        if (connector instanceof SQLiteConnector) {
            sql = "INSERT INTO player_stats (player_uuid, player_name, events_won, events_participated, total_rewards) " +
                  "VALUES (?, ?, ?, 1, ?) " +
                  "ON CONFLICT(player_uuid) DO UPDATE SET " +
                  "player_name = ?, " +
                  "events_won = events_won + ?, " +
                  "events_participated = events_participated + 1, " +
                  "total_rewards = total_rewards + ?";
        }

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int wonValue = won ? 1 : 0;
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, playerName);
            stmt.setInt(3, wonValue);
            stmt.setInt(4, wonValue);
            stmt.setString(5, playerName);
            stmt.setInt(6, wonValue);
            stmt.setInt(7, wonValue);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update player stats", e);
        }
    }

    public Optional<PlayerStats> getPlayerStats(UUID playerUuid) {
        String sql = "SELECT * FROM player_stats WHERE player_uuid = ?";
        
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerUuid.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new PlayerStats(
                        playerUuid,
                        rs.getString("player_name"),
                        rs.getInt("events_won"),
                        rs.getInt("events_participated"),
                        rs.getTimestamp("last_win"),
                        rs.getInt("total_rewards")
                    ));
                }
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get player stats", e);
        }
        
        return Optional.empty();
    }

    public List<EventRecord> getRecentEvents(int limit) {
        String sql = "SELECT * FROM event_records ORDER BY event_time DESC LIMIT ?";
        List<EventRecord> events = new ArrayList<>();
        
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get recent events", e);
        }
        
        return events;
    }

    public List<PlayerStats> getTopPlayers(int limit) {
        String sql = "SELECT * FROM player_stats ORDER BY events_won DESC LIMIT ?";
        List<PlayerStats> players = new ArrayList<>();
        
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    players.add(new PlayerStats(
                        UUID.fromString(rs.getString("player_uuid")),
                        rs.getString("player_name"),
                        rs.getInt("events_won"),
                        rs.getInt("events_participated"),
                        rs.getTimestamp("last_win"),
                        rs.getInt("total_rewards")
                    ));
                }
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get top players", e);
        }
        
        return players;
    }
}