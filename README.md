# MsnAirDrop

A Minecraft plugin that creates exciting AirDrop events with customizable rewards and statistics tracking.

## Features

- Customizable AirDrop events with configurable locations and timers
- Complete statistics system tracking player participation and wins
- PlaceholderAPI integration for scoreboard support
- MySQL/SQLite database support
- In-game reward management
- Multi-language support
- MiniMessage formatting support

## Commands

- `/msnairdrop version` - Show plugin version
- `/msnairdrop reload` - Reload configuration
- `/msnairdrop pos1` - Set first position for AirDrop zone
- `/msnairdrop pos2` - Set second position for AirDrop zone
- `/msnairdrop event` - Manage AirDrop events
- `/msnairdrop debug` - Toggle debug mode
- `/msnairdrop reward` - Manage rewards
- `/airdropstats [player]` - View player statistics

## Permissions

- `msnairdrop.admin` - Access to admin commands
- `msnairdrop.stats` - Access to view statistics
- `msnairdrop.reload` - Permission to reload plugin
- `msnairdrop.event` - Permission to manage events
- `msnairdrop.reward` - Permission to manage rewards

## Configuration

All configuration files are located in the plugin's data folder:
- `config.yml` - Main configuration
- `messages/` - Language files
- `drops/` - Reward configurations

## Dependencies

- PlaceholderAPI
- HikariCP (included)
- Adventure API (included)

## Installation

1. Place the plugin JAR in your server's `plugins` folder
2. Restart your server
3. Configure the plugin in `plugins/MsnAirDrop/config.yml`
4. Use `/msnairdrop reload` to apply changes

## Database Configuration

The plugin supports both MySQL and SQLite:

### SQLite
- Enabled by default
- No configuration needed
- Data stored in `plugins/MsnAirDrop/database.db`

### MySQL
```yaml
database:
  type: mysql
  host: localhost
  port: 3306
  database: minecraft
  username: your_username
  password: your_password
  pool-size: 10
```

## Support

For issues and feature requests, please use the GitHub issues tracker.