# 🎁 MsnAirDrop v1.0.0 - Advanced AirDrop System

Hey there! 👋 Welcome to MsnAirDrop, a feature-rich and exciting AirDrop plugin for Paper/Spigot servers. Create thrilling events where players compete for valuable rewards while tracking their statistics!

## ✨ Features

- 🎮 **Dynamic Events** - Create exciting AirDrop events with customizable locations and timers
- 📊 **Statistics System** - Track player participation, wins, and detailed combat stats
- 🏆 **Reward Management** - Customize rewards with different tiers and probabilities
- 🗄️ **Database Choice** - Support for both SQLite and MySQL databases
- 🔌 **PlaceholderAPI Support** - Display stats on scoreboards and in-game
- 🌍 **Multi-language Support** - Easy language customization
- 💬 **MiniMessage Format** - Beautiful formatted messages with RGB support
- ⚡ **High Performance** - Async database operations and optimized code
- 🎨 **Custom Messages** - Fully customizable messages and announcements

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

### 🎯 Features in v1.0.0 (Initial Release)

- ✅ **Complete Stats System** - Track kills, deaths, wins, and more
- ✅ **Database Integration** - Reliable data storage with connection pooling
- ✅ **Event Management** - Full control over AirDrop events
- ✅ **Combat Statistics** - Track player performance in events
- ✅ **PlaceholderAPI** - Display stats anywhere
- ✅ **Multi-Database** - Choose between MySQL and SQLite
- ✅ **Async Operations** - Smooth server performance
- ✅ **Custom Messages** - Full message customization

## 🚀 Installation

1. Download the latest release from [GitHub Releases](https://github.com/msncakma/msnAirDrop/releases)
2. Place the .jar file in your server's `plugins` folder
3. Install required dependencies (PlaceholderAPI)
4. Restart your server
5. Configure the plugin in `config.yml`
6. Use `/msnairdrop reload` to apply changes

## 📦 Dependencies

- **Required:**
  - PlaceholderAPI (for stats display)
  - Paper/Spigot 1.19.4+
  - Java 17 or higher

- **Included:**
  - HikariCP (database connection pooling)
  - Adventure API (message formatting)

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