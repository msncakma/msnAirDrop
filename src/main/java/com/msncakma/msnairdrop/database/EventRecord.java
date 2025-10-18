package com.msncakma.msnairdrop.database;

import java.sql.Timestamp;
import java.util.UUID;

public class EventRecord {
    private final int id;
    private final Timestamp eventTime;
    private final String world;
    private final int x;
    private final int y;
    private final int z;
    private final UUID winnerUuid;
    private final String winnerName;
    private final String rewardName;
    private final boolean testMode;

    public EventRecord(int id, Timestamp eventTime, String world, int x, int y, int z,
                      UUID winnerUuid, String winnerName, String rewardName, boolean testMode) {
        this.id = id;
        this.eventTime = eventTime;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.winnerUuid = winnerUuid;
        this.winnerName = winnerName;
        this.rewardName = rewardName;
        this.testMode = testMode;
    }

    // Getters
    public int getId() { return id; }
    public Timestamp getEventTime() { return eventTime; }
    public String getWorld() { return world; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public UUID getWinnerUuid() { return winnerUuid; }
    public String getWinnerName() { return winnerName; }
    public String getRewardName() { return rewardName; }
    public boolean isTestMode() { return testMode; }
}