package com.msncakma.msnairdrop.commands;

import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand implements CommandExecutor {
    private final MsnAirDrop plugin;

    public DebugCommand(MsnAirDrop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("msnairdrop.admin")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("prefix") + " " +
                             plugin.getLanguageManager().getMessage("commands.no-permission"));
            return true;
        }

        String prefix = plugin.getLanguageManager().getMessage("prefix");

        if (args.length < 1) {
            sendHelp(sender, prefix);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "mode":
                handleDebugMode(sender, args, prefix);
                break;
            case "toggle":
                handleDebugToggle(sender, prefix);
                break;
            case "info":
                handleDebugInfo(sender, prefix);
                break;
            default:
                sendHelp(sender, prefix);
                break;
        }

        return true;
    }

    private void handleDebugMode(CommandSender sender, String[] args, String prefix) {
        if (args.length < 2) {
            sender.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("commands.debug.usage-mode"));
            return;
        }

        boolean enable = args[1].equalsIgnoreCase("on");
        plugin.getDebugManager().setDebugMode(enable);
        
        String message = enable ? 
            plugin.getLanguageManager().getMessage("admin.debug-mode-enabled") :
            plugin.getLanguageManager().getMessage("admin.debug-mode-disabled");
        
        sender.sendMessage(prefix + " " + message);
    }

    private void handleDebugToggle(CommandSender sender, String prefix) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("errors.player-only"));
            return;
        }

        Player player = (Player) sender;
        plugin.getDebugManager().toggleDebug(player);
    }

    private void handleDebugInfo(CommandSender sender, String prefix) {
        sender.sendMessage(prefix + " §6=== Debug Information ===");
        sender.sendMessage("§7Debug Mode: " + (plugin.getDebugManager().isDebugMode() ? "§aEnabled" : "§cDisabled"));
        
        if (sender instanceof Player) {
            Player player = (Player) sender;
            sender.sendMessage("§7Personal Debug: " + 
                (plugin.getDebugManager().isDebugEnabled(player) ? "§aEnabled" : "§cDisabled"));
        }
        
        sender.sendMessage("§7Version: §f" + plugin.getDescription().getVersion());
        if (plugin.getUpdateChecker() != null && plugin.getUpdateChecker().isUpdateAvailable()) {
            sender.sendMessage("§7Latest Version: §f" + plugin.getUpdateChecker().getLatestVersion());
        }
    }

    private void sendHelp(CommandSender sender, String prefix) {
        sender.sendMessage(prefix + " §6=== Debug Commands ===");
        sender.sendMessage("§e/msnairdrop debug mode <on|off> §7- Enable/disable debug mode");
        sender.sendMessage("§e/msnairdrop debug toggle §7- Toggle personal debug messages");
        sender.sendMessage("§e/msnairdrop debug info §7- Show debug information");
    }
}