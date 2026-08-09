package com.xanemidev.login.commands;

import com.xanemidev.login.CentralLoginPlugin;
import com.xanemidev.login.ui.RegisterUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterCommand implements CommandExecutor {

    private final CentralLoginPlugin plugin;

    public RegisterCommand(CentralLoginPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players!");
            return true;
        }

        Player player = (Player) sender;

        if (plugin.getDatabaseManager().isPlayerRegistered(player.getUniqueId().toString())) {
            player.sendMessage("\u00a76[Central-Login] \u00a7cYou are already registered!");
            return true;
        }

        new RegisterUI(plugin, player).show();
        return true;
    }
}