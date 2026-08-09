package com.xanemidev.login.listeners;

import com.xanemidev.login.CentralLoginPlugin;
import com.xanemidev.login.ui.LoginUI;
import com.xanemidev.login.ui.RegisterUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final CentralLoginPlugin plugin;

    public PlayerJoinListener(CentralLoginPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        // Check if player has permission to bypass
        if (player.hasPermission("central-login.bypass")) {
            return;
        }

        // Check if player is registered
        boolean isRegistered = plugin.getDatabaseManager().isPlayerRegistered(uuid);

        if (isRegistered) {
            // Show login UI
            new LoginUI(plugin, player).show();
        } else {
            // Show registration UI
            new RegisterUI(plugin, player).show();
        }
    }
}