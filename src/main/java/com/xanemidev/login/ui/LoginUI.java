package com.xanemidev.login.ui;

import com.xanemidev.login.CentralLoginPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoginUI implements Listener {

    private final CentralLoginPlugin plugin;
    private final Player player;
    private boolean awaitingPassword = false;

    public LoginUI(CentralLoginPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void show() {
        // Create inventory for login UI
        Inventory inv = Bukkit.createInventory(null, 27, "\u00a76Central-Login - Login");

        // Add decorative glass panes
        ItemStack glass = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        for (int i = 0; i < 27; i++) {
            if (i != 13) {
                inv.setItem(i, glass);
            }
        }

        // Add login button (center)
        ItemStack loginButton = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta loginMeta = loginButton.getItemMeta();
        loginMeta.setDisplayName("\u00a7aClick to Enter Password");
        loginButton.setItemMeta(loginMeta);
        inv.setItem(13, loginButton);

        player.openInventory(inv);
        player.sendMessage("\u00a76[Central-Login] \u00a7fPlease enter your password in chat.");
        awaitingPassword = true;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(player) || !awaitingPassword) {
            return;
        }

        event.setCancelled(true);
        String password = event.getMessage();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (plugin.getDatabaseManager().verifyPassword(player.getUniqueId().toString(), password)) {
                // Login successful
                plugin.getSessionManager().addSession(player.getUniqueId());
                player.sendMessage("\u00a76[Central-Login] \u00a7aLogin successful! Welcome back, " + player.getName() + "!");
                player.closeInventory();
                awaitingPassword = false;
                plugin.getDatabaseManager().updateLastLogin(player.getUniqueId().toString());
            } else {
                // Login failed
                plugin.getSessionManager().recordFailedAttempt(player.getUniqueId());
                int remainingAttempts = plugin.getSessionManager().getRemainingAttempts(player.getUniqueId());
                
                if (plugin.getSessionManager().isLockedOut(player.getUniqueId())) {
                    player.sendMessage("\u00a76[Central-Login] \u00a7cToo many failed attempts! You are locked out.");
                    player.kick("\u00a7cLogin locked out due to too many failed attempts.");
                } else {
                    player.sendMessage("\u00a76[Central-Login] \u00a7cIncorrect password! Attempts remaining: " + remainingAttempts);
                    show(); // Show UI again
                }
            }
        });
    }
}