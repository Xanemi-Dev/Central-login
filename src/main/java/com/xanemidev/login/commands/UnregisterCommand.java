package com.xanemidev.login.commands;

import com.xanemidev.login.CentralLoginPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnregisterCommand implements CommandExecutor {

    private final CentralLoginPlugin plugin;

    public UnregisterCommand(CentralLoginPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("central-login.admin")) {
            sender.sendMessage("\u00a7cYou do not have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /unregister <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("\u00a7cPlayer not found!");
            return true;
        }

        if (plugin.getDatabaseManager().deletePlayer(target.getUniqueId().toString())) {
            plugin.getSessionManager().removeSession(target.getUniqueId());
            sender.sendMessage("\u00a76[Central-Login] \u00a7aPlayer " + target.getName() + " has been unregistered!");
            target.sendMessage("\u00a76[Central-Login] \u00a7cYou have been unregistered by an administrator!");
        } else {
            sender.sendMessage("\u00a7cFailed to unregister player!");
        }

        return true;
    }
}