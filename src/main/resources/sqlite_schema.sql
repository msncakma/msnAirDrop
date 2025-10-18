-- SQLite schema for MsnAirDrop zone statistics
CREATE TABLE IF NOT EXISTS event_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    world TEXT NOT NULL,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z INTEGER NOT NULL,
    winner_uuid TEXT NOT NULL,
    winner_name TEXT NOT NULL,
    reward_name TEXT NOT NULL,
    test_mode INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_winner_uuid ON event_records(winner_uuid);
CREATE INDEX IF NOT EXISTS idx_event_time ON event_records(event_time);

CREATE TABLE IF NOT EXISTS player_stats (
    player_uuid TEXT PRIMARY KEY,
    player_name TEXT NOT NULL,
    events_won INTEGER NOT NULL DEFAULT 0,
    events_participated INTEGER NOT NULL DEFAULT 0,
    total_rewards INTEGER NOT NULL DEFAULT 0,
    zone_kills INTEGER NOT NULL DEFAULT 0,
    zone_deaths INTEGER NOT NULL DEFAULT 0,
    golden_apples_used INTEGER NOT NULL DEFAULT 0,
    damage_dealt INTEGER NOT NULL DEFAULT 0,
    damage_taken INTEGER NOT NULL DEFAULT 0,
    fireworks_used INTEGER NOT NULL DEFAULT 0,
    time_in_zone INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_player_name ON player_stats(player_name);

CREATE TABLE IF NOT EXISTS zone_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_uuid TEXT NOT NULL,
    event_id INTEGER NOT NULL,
    entry_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exit_time TIMESTAMP,
    kills INTEGER NOT NULL DEFAULT 0,
    deaths INTEGER NOT NULL DEFAULT 0,
    golden_apples INTEGER NOT NULL DEFAULT 0,
    damage_dealt INTEGER NOT NULL DEFAULT 0,
    damage_taken INTEGER NOT NULL DEFAULT 0,
    fireworks INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (event_id) REFERENCES event_records(id),
    FOREIGN KEY (player_uuid) REFERENCES player_stats(player_uuid)
);

CREATE INDEX IF NOT EXISTS idx_zone_sessions_player ON zone_sessions(player_uuid);
CREATE INDEX IF NOT EXISTS idx_zone_sessions_event ON zone_sessions(event_id);