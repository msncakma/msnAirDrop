package com.msncakma.msnairdrop.commands;

import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final MsnAirDrop plugin;

    public ReloadCommand(MsnAirDrop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("msnairdrop.admin")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("prefix") + " " +
                             plugin.getLanguageManager().getMessage("commands.no-permission"));
            return true;
        }

        try {
            // Reload configurations
            plugin.reloadConfig();
            
            // Reload managers
            plugin.getLanguageManager().reload();
            plugin.getDropManager().reload();
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("prefix") + " " +
                             plugin.getLanguageManager().getMessage("commands.reload-success"));
            
            // Check for updates after reload
            if (plugin.getConfig().getBoolean("settings.check-updates", true)) {
                plugin.checkForUpdates();
            }
            
            return true;
        } catch (Exception e) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("prefix") + " " +
                             plugin.getLanguageManager().getMessage("commands.reload-failed"));
            plugin.getLogger().severe("Failed to reload plugin: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
}