package com.msncakma.msnairdrop.commands;

import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VersionCommand implements CommandExecutor {
    private final MsnAirDrop plugin;

    public VersionCommand(MsnAirDrop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getLanguageManager().getMessage("prefix");
        
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(prefix + ChatColor.GRAY + " Available commands:");
            sender.sendMessage(ChatColor.AQUA + "/" + label + " version" + ChatColor.GRAY + " - Show plugin version");
            sender.sendMessage(ChatColor.AQUA + "/" + label + " help" + ChatColor.GRAY + " - Show this help message");
            return true;
        }

        if (args[0].equalsIgnoreCase("version")) {
            if (!sender.hasPermission("msnairdrop.version")) {
                sender.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("commands.no-permission"));
                return true;
            }
            
            String version = plugin.getDescription().getVersion();
            sender.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("commands.version.info", "version", version));
            sender.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("commands.version.author", "author", "msncakma"));
            sender.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("commands.version.github", "github", "https://github.com/msncakma"));
            return true;
        }

        sender.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("commands.unknown-command", "command", label));
        return true;
    }
}