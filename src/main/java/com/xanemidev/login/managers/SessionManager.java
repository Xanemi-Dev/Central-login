package com.xanemidev.login.managers;

import com.xanemidev.login.CentralLoginPlugin;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SessionManager {

    private final CentralLoginPlugin plugin;
    private final Map<UUID, Long> loggedInPlayers = new HashMap<>();
    private final Map<UUID, Integer> failedAttempts = new HashMap<>();
    private final int SESSION_TIMEOUT = 30; // minutes
    private final int MAX_ATTEMPTS = 3;
    private final int LOCKOUT_TIME = 15; // minutes

    public SessionManager(CentralLoginPlugin plugin) {
        this.plugin = plugin;
        startSessionCheck();
    }

    public void addSession(UUID uuid) {
        loggedInPlayers.put(uuid, System.currentTimeMillis());
        failedAttempts.remove(uuid);
    }

    public boolean isLoggedIn(UUID uuid) {
        return loggedInPlayers.containsKey(uuid);
    }

    public void removeSession(UUID uuid) {
        loggedInPlayers.remove(uuid);
    }

    public void recordFailedAttempt(UUID uuid) {
        int attempts = failedAttempts.getOrDefault(uuid, 0) + 1;
        failedAttempts.put(uuid, attempts);
    }

    public boolean isLockedOut(UUID uuid) {
        return failedAttempts.getOrDefault(uuid, 0) >= MAX_ATTEMPTS;
    }

    public int getRemainingAttempts(UUID uuid) {
        return Math.max(0, MAX_ATTEMPTS - failedAttempts.getOrDefault(uuid, 0));
    }

    public void resetFailedAttempts(UUID uuid) {
        failedAttempts.remove(uuid);
    }

    private void startSessionCheck() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            long timeoutMillis = TimeUnit.MINUTES.toMillis(SESSION_TIMEOUT);
            
            loggedInPlayers.entrySet().removeIf(entry -> 
                (currentTime - entry.getValue()) > timeoutMillis
            );
        }, 0L, 1200L); // Check every minute
    }
}