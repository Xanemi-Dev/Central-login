package com.xanemidev.login;

import com.xanemidev.login.listeners.PlayerJoinListener;
import com.xanemidev.login.listeners.PlayerQuitListener;
import com.xanemidev.login.commands.LoginCommand;
import com.xanemidev.login.commands.RegisterCommand;
import com.xanemidev.login.commands.UnregisterCommand;
import com.xanemidev.login.commands.LoginAdminCommand;
import com.xanemidev.login.managers.DatabaseManager;
import com.xanemidev.login.managers.SessionManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CentralLoginPlugin extends JavaPlugin {

    private DatabaseManager databaseManager;
    private SessionManager sessionManager;

    @Override
    public void onEnable() {
        getLogger().info("\"Central-Login plugin is enabling...\"");

        // Initialize configuration
        saveDefaultConfig();

        // Initialize managers
        this.databaseManager = new DatabaseManager(this);
        this.sessionManager = new SessionManager(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        // Register commands
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("unregister").setExecutor(new UnregisterCommand(this));
        getCommand("loginadmin").setExecutor(new LoginAdminCommand(this));

        getLogger().info("Central-Login plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Central-Login plugin is disabling...");
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("Central-Login plugin disabled!");
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}