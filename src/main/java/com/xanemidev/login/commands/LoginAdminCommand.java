package com.xanemidev.login.commands;

import com.xanemidev.login.CentralLoginPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LoginAdminCommand implements CommandExecutor {

    private final CentralLoginPlugin plugin;

    public LoginAdminCommand(CentralLoginPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("central-login.admin")) {
            sender.sendMessage("\u00a7cYou do not have permission to use this command!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /loginadmin <reload|reset>");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "reload":
                plugin.reloadConfig();
                sender.sendMessage("\u00a76[Central-Login] \u00a7aConfiguration reloaded!");
                break;
            case "reset":
                sender.sendMessage("\u00a76[Central-Login] \u00a7cThis command will reset all login data. Are you sure? (Use /loginadmin reset confirm)");
                break;
            default:
                sender.sendMessage("Unknown subcommand! Usage: /loginadmin <reload|reset>");
        }

        return true;
    }
}