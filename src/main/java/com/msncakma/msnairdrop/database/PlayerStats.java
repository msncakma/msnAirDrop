package com.msncakma.msnairdrop.database;

import java.util.UUID;

public class PlayerStats {
    private final UUID playerUuid;
    private final String playerName;
    private final int eventsWon;
    private final int eventsParticipated;
    private final int totalRewards;
    private final java.sql.Timestamp lastWin;

    public PlayerStats(UUID playerUuid, String playerName, int eventsWon, 
                      int eventsParticipated, java.sql.Timestamp lastWin,
                      int totalRewards) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.eventsWon = eventsWon;
        this.eventsParticipated = eventsParticipated;
        this.lastWin = lastWin;
        this.totalRewards = totalRewards;
    }

    // Getters
    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public int getEventsWon() { return eventsWon; }
    public int getEventsParticipated() { return eventsParticipated; }
    public int getTotalRewards() { return totalRewards; }

    public double getWinRate() {
        if (eventsParticipated == 0) return 0.0;
        return (double) eventsWon / eventsParticipated * 100;
    }
}