package com.xanemidev.login.listeners;

import com.xanemidev.login.CentralLoginPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final CentralLoginPlugin plugin;

    public PlayerQuitListener(CentralLoginPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getSessionManager().removeSession(player.getUniqueId());
    }
}