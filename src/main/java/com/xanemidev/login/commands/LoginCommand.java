package com.xanemidev.login.commands;

import com.xanemidev.login.CentralLoginPlugin;
import com.xanemidev.login.ui.LoginUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LoginCommand implements CommandExecutor {

    private final CentralLoginPlugin plugin;

    public LoginCommand(CentralLoginPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players!");
            return true;
        }

        Player player = (Player) sender;

        if (plugin.getSessionManager().isLoggedIn(player.getUniqueId())) {
            player.sendMessage("\u00a76[Central-Login] \u00a7aYou are already logged in!");
            return true;
        }

        new LoginUI(plugin, player).show();
        return true;
    }
}