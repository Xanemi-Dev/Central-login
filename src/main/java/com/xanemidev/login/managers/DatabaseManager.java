package com.xanemidev.login.managers;

import com.xanemidev.login.CentralLoginPlugin;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {

    private final CentralLoginPlugin plugin;
    private Connection connection;
    private final String dbPath;

    public DatabaseManager(CentralLoginPlugin plugin) {
        this.plugin = plugin;
        this.dbPath = new File(plugin.getDataFolder(), "players.db").getAbsolutePath();
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTables();
            plugin.getLogger().info("Database initialized successfully!");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        String playerTable = "CREATE TABLE IF NOT EXISTS players (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "uuid TEXT UNIQUE NOT NULL," +
                "username TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "email TEXT," +
                "registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "last_login TIMESTAMP" +
                ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(playerTable);
        }
    }

    public boolean isPlayerRegistered(String uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM players WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            plugin.getLogger().warning("Error checking player registration: " + e.getMessage());
            return false;
        }
    }

    public boolean registerPlayer(String uuid, String username, String hashedPassword, String email) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO players (uuid, username, password, email) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, uuid);
            stmt.setString(2, username);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, email);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().warning("Error registering player: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyPassword(String uuid, String inputPassword) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT password FROM players WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return BCryptManager.verifyPassword(inputPassword, storedPassword);
            }
            return false;
        } catch (SQLException e) {
            plugin.getLogger().warning("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    public boolean updateLastLogin(String uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE players SET last_login = CURRENT_TIMESTAMP WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().warning("Error updating last login: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePlayer(String uuid) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM players WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().warning("Error deleting player: " + e.getMessage());
            return false;
        }
    }

    public Map<String, String> getPlayerInfo(String uuid) {
        Map<String, String> info = new HashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT username, email, registered_at, last_login FROM players WHERE uuid = ?")) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                info.put("username", rs.getString("username"));
                info.put("email", rs.getString("email"));
                info.put("registered_at", rs.getString("registered_at"));
                info.put("last_login", rs.getString("last_login"));
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Error getting player info: " + e.getMessage());
        }
        return info;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Database connection closed!");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Error closing database: " + e.getMessage());
        }
    }
}