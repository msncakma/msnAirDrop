package com.msncakma.msnairdrop.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.entity.Player;

public class ZoneStats {
    private final DatabaseConnector connector;
    private final MsnAirDrop plugin;

    public ZoneStats(DatabaseConnector connector, MsnAirDrop plugin) {
        this.connector = connector;
        this.plugin = plugin;
    }

    public void startZoneSession(UUID playerUuid, int eventId) {
        String sql = "INSERT INTO zone_sessions (player_uuid, event_id) VALUES (?, ?)";
        
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerUuid.toString());
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to start zone session", e);
        }
    }

    public void endZoneSession(UUID playerUuid, int eventId) {
        String sql = "UPDATE zone_sessions SET exit_time = CURRENT_TIMESTAMP " +
                    "WHERE player_uuid = ? AND event_id = ? AND exit_time IS NULL";
        
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerUuid.toString());
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to end zone session", e);
        }
    }

    public void recordKill(UUID playerUuid, int eventId) {
        updateSessionStat(playerUuid, eventId, "kills");
        updatePlayerStat(playerUuid, "zone_kills");
    }

    public void recordDeath(UUID playerUuid, int eventId) {
        updateSessionStat(playerUuid, eventId, "deaths");
        updatePlayerStat(playerUuid, "zone_deaths");
    }

    public void recordGoldenApple(UUID playerUuid, int eventId) {
        updateSessionStat(playerUuid, eventId, "golden_apples");
        updatePlayerStat(playerUuid, "golden_apples_used");
    }

    public void recordDamageDealt(UUID playerUuid, int eventId, int amount) {
        updateSessionStatByAmount(playerUuid, eventId, "damage_dealt", amount);
        updatePlayerStatByAmount(playerUuid, "damage_dealt", amount);
    }

    public void recordDamageTaken(UUID playerUuid, int eventId, int amount) {
        updateSessionStatByAmount(playerUuid, eventId, "damage_taken", amount);
        updatePlayerStatByAmount(playerUuid, "damage_taken", amount);
    }

    public void recordFirework(UUID playerUuid, int eventId) {
        updateSessionStat(playerUuid, eventId, "fireworks");
        updatePlayerStat(playerUuid, "fireworks_used");
    }

    private void updateSessionStat(UUID playerUuid, int eventId, String statName) {
        String sql = "UPDATE zone_sessions SET " + statName + " = " + statName + " + 1 " +
                    "WHERE player_uuid = ? AND event_id = ? AND exit_time IS NULL";
        
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerUuid.toString());
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update session stat: " + statName, e);
        }
    }

    private void updateSessionStatByAmount(UUID playerUuid, int eventId, String statName, int amount) {
        String sql = "UPDATE zone_sessions SET " + statName + " = " + statName + " + ? " +
                    "WHERE player_uuid = ? AND event_id = ? AND exit_time IS NULL";
        
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, amount);
            stmt.setString(2, playerUuid.toString());
            stmt.setInt(3, eventId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update session stat: " + statName, e);
        }
    }

    private void updatePlayerStat(UUID playerUuid, String statName) {
        String sql = "UPDATE player_stats SET " + statName + " = " + statName + " + 1 " +
                    "WHERE player_uuid = ?";
        
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerUuid.toString());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update player stat: " + statName, e);
        }
    }

    private void updatePlayerStatByAmount(UUID playerUuid, String statName, int amount) {
        String sql = "UPDATE player_stats SET " + statName + " = " + statName + " + ? " +
                    "WHERE player_uuid = ?";
        
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, amount);
            stmt.setString(2, playerUuid.toString());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update player stat: " + statName, e);
        }
    }

    // Methods for retrieving statistics
    public int getZoneKills(UUID playerUuid) {
        return getPlayerStat(playerUuid, "zone_kills");
    }

    public int getZoneDeaths(UUID playerUuid) {
        return getPlayerStat(playerUuid, "zone_deaths");
    }

    public double getKDRatio(UUID playerUuid) {
        int deaths = getZoneDeaths(playerUuid);
        return deaths == 0 ? getZoneKills(playerUuid) : (double) getZoneKills(playerUuid) / deaths;
    }

    public int getGoldenApplesUsed(UUID playerUuid) {
        return getPlayerStat(playerUuid, "golden_apples_used");
    }

    public int getDamageDealt(UUID playerUuid) {
        return getPlayerStat(playerUuid, "damage_dealt");
    }

    public int getDamageTaken(UUID playerUuid) {
        return getPlayerStat(playerUuid, "damage_taken");
    }

    public int getFireworksUsed(UUID playerUuid) {
        return getPlayerStat(playerUuid, "fireworks_used");
    }

    private int getPlayerStat(UUID playerUuid, String statName) {
        String sql = "SELECT " + statName + " FROM player_stats WHERE player_uuid = ?";
        
        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, playerUuid.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(statName);
                }
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get player stat: " + statName, e);
        }
        
        return 0;
    }
}