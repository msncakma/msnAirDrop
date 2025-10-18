-- MySQL schema for MsnAirDrop zone statistics
CREATE TABLE IF NOT EXISTS event_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    event_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    world VARCHAR(64) NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL,
    z INT NOT NULL,
    winner_uuid CHAR(36) NOT NULL,
    winner_name VARCHAR(16) NOT NULL,
    reward_name VARCHAR(64) NOT NULL,
    test_mode BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_winner_uuid (winner_uuid),
    INDEX idx_event_time (event_time)
);

CREATE TABLE IF NOT EXISTS player_stats (
    player_uuid CHAR(36) PRIMARY KEY,
    player_name VARCHAR(16) NOT NULL,
    events_won INT NOT NULL DEFAULT 0,
    events_participated INT NOT NULL DEFAULT 0,
    total_rewards INT NOT NULL DEFAULT 0,
    zone_kills INT NOT NULL DEFAULT 0,
    zone_deaths INT NOT NULL DEFAULT 0,
    golden_apples_used INT NOT NULL DEFAULT 0,
    damage_dealt INT NOT NULL DEFAULT 0,
    damage_taken INT NOT NULL DEFAULT 0,
    fireworks_used INT NOT NULL DEFAULT 0,
    time_in_zone INT NOT NULL DEFAULT 0,
    INDEX idx_player_name (player_name)
);

CREATE TABLE IF NOT EXISTS zone_sessions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    player_uuid CHAR(36) NOT NULL,
    event_id INT NOT NULL,
    entry_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    exit_time TIMESTAMP NULL,
    kills INT NOT NULL DEFAULT 0,
    deaths INT NOT NULL DEFAULT 0,
    golden_apples INT NOT NULL DEFAULT 0,
    damage_dealt INT NOT NULL DEFAULT 0,
    damage_taken INT NOT NULL DEFAULT 0,
    fireworks INT NOT NULL DEFAULT 0,
    FOREIGN KEY (event_id) REFERENCES event_records(id),
    FOREIGN KEY (player_uuid) REFERENCES player_stats(player_uuid),
    INDEX idx_zone_sessions_player (player_uuid),
    INDEX idx_zone_sessions_event (event_id)
);