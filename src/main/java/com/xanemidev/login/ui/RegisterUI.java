package com.xanemidev.login.ui;

import com.xanemidev.login.CentralLoginPlugin;
import com.xanemidev.login.managers.BCryptManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RegisterUI implements Listener {

    private final CentralLoginPlugin plugin;
    private final Player player;
    private int registrationStep = 0; // 0 = password, 1 = confirm password, 2 = email
    private String password = "";

    public RegisterUI(CentralLoginPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void show() {
        // Create inventory for registration UI
        Inventory inv = Bukkit.createInventory(null, 27, "\u00a76Central-Login - Register");

        // Add decorative glass panes
        ItemStack glass = new ItemStack(Material.LIGHT_GREEN_STAINED_GLASS_PANE);
        for (int i = 0; i < 27; i++) {
            if (i != 13) {
                inv.setItem(i, glass);
            }
        }

        // Add registration button (center)
        ItemStack registerButton = new ItemStack(Material.YELLOW_CONCRETE);
        ItemMeta registerMeta = registerButton.getItemMeta();
        registerMeta.setDisplayName("\u00a7eClick to Begin Registration");
        registerButton.setItemMeta(registerMeta);
        inv.setItem(13, registerButton);

        player.openInventory(inv);
        player.sendMessage("\u00a76[Central-Login] \u00a7fWelcome! You need to register. Please follow the prompts in chat.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(player)) {
            return;
        }

        if (registrationStep == 0) {
            event.setCancelled(true);
            password = event.getMessage();
            
            if (password.length() < 6) {
                player.sendMessage("\u00a76[Central-Login] \u00a7cPassword must be at least 6 characters long!");
                return;
            }
            
            registrationStep = 1;
            player.sendMessage("\u00a76[Central-Login] \u00a7fPlease confirm your password by typing it again.");
        } else if (registrationStep == 1) {
            event.setCancelled(true);
            String confirmPassword = event.getMessage();
            
            if (!confirmPassword.equals(password)) {
                player.sendMessage("\u00a76[Central-Login] \u00a7cPasswords do not match! Try again.");
                registrationStep = 0;
                password = "";
                show();
                return;
            }
            
            registrationStep = 2;
            player.sendMessage("\u00a76[Central-Login] \u00a7fNow enter your email address (or type 'skip' to skip).");
        } else if (registrationStep == 2) {
            event.setCancelled(true);
            String email = event.getMessage();
            
            if (email.equalsIgnoreCase("skip")) {
                email = "";
            } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                player.sendMessage("\u00a76[Central-Login] \u00a7cInvalid email format! Try again or type 'skip'.");
                return;
            }
            
            completeRegistration(email);
        }
    }

    private void completeRegistration(String email) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            String uuid = player.getUniqueId().toString();
            String hashedPassword = BCryptManager.hashPassword(password);
            
            if (plugin.getDatabaseManager().registerPlayer(uuid, player.getName(), hashedPassword, email)) {
                plugin.getSessionManager().addSession(player.getUniqueId());
                player.sendMessage("\u00a76[Central-Login] \u00a7aRegistration successful! You are now logged in.");
                player.closeInventory();
                registrationStep = 0;
                password = "";
            } else {
                player.sendMessage("\u00a76[Central-Login] \u00a7cRegistration failed! Please try again later.");
            }
        });
    }
}