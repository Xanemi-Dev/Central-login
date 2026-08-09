# Central-Login

A lightweight, minimalistic login and registration plugin for Minecraft servers (version 1.21.1).

## Features

✨ **Minimalistic UI**: Clean and intuitive inventory-based UIs for login and registration

🔐 **Secure**: Password hashing with secure salt generation

⚡ **Lightweight**: Minimal performance impact on your server

🛡️ **Account Protection**: Failed login attempt tracking and lockout system

📧 **Email Support**: Optional email registration for account recovery

💾 **SQLite Database**: Built-in SQLite database for player data storage

## Installation

1. Build the plugin using Maven:
   ```bash
   mvn clean package
   ```

2. Copy the generated `.jar` file to your server's `plugins` folder

3. Restart your server

4. Configure the plugin in `plugins/Central-Login/config.yml`

## Commands

### Player Commands
- `/login` - Open the login UI
- `/register` - Open the registration UI

### Admin Commands
- `/unregister <player>` - Unregister a player account
- `/loginadmin reload` - Reload the configuration
- `/loginadmin reset` - Reset all login data

## Permissions

- `central-login.admin` - Access to admin commands (default: op)
- `central-login.bypass` - Bypass login requirement (default: false)

## Configuration

Edit `plugins/Central-Login/config.yml` to customize:

- Session timeout duration
- Maximum login attempts
- Lockout time after failed attempts
- Email requirement
- UI colors and messages

## How It Works

### First Login (Registration)
1. Player joins the server
2. Registration UI is displayed
3. Player enters password (minimum 6 characters)
4. Player confirms password
5. Player enters email (optional)
6. Account is created and player is logged in

### Subsequent Logins
1. Player joins the server
2. Login UI is displayed
3. Player enters password
4. Password is verified against stored hash
5. On success: Player is logged in
6. On failure: Failed attempt is recorded, player can retry (max 3 attempts)

## Security Features

- Passwords are hashed using secure salt-based algorithm
- Failed login attempts are tracked
- After 3 failed attempts, player is locked out for 15 minutes
- Session timeout after 30 minutes of inactivity
- Admin-only commands for account management

## Database

The plugin uses SQLite for data storage. Player data is stored in:
- Location: `plugins/Central-Login/players.db`
- Format: SQLite database

## Support

For issues and feature requests, visit: https://github.com/Xanemi-Dev/Central-login

## License

This project is open source and available on GitHub.
