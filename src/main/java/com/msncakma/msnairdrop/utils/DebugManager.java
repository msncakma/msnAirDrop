package com.msncakma.msnairdrop.utils;

import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class DebugManager {
    private final MsnAirDrop plugin;
    private final Set<UUID> debugPlayers;
    private boolean debugMode;

    public DebugManager(MsnAirDrop plugin) {
        this.plugin = plugin;
        this.debugPlayers = new HashSet<>();
        this.debugMode = plugin.getConfig().getBoolean("settings.debug-mode", false);
    }

    public void setDebugMode(boolean enabled) {
        this.debugMode = enabled;
        plugin.getConfig().set("settings.debug-mode", enabled);
        plugin.saveConfig();
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void toggleDebug(Player player) {
        UUID playerId = player.getUniqueId();
        String prefix = plugin.getLanguageManager().getMessage("prefix");

        if (debugPlayers.contains(playerId)) {
            debugPlayers.remove(playerId);
            player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("admin.debug-disabled"));
        } else {
            debugPlayers.add(playerId);
            player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("admin.debug-enabled"));
        }
    }

    public void debug(String message) {
        if (!debugMode) {
            return;
        }

        // Log to console
        plugin.getLogger().log(Level.INFO, "[Debug] " + message);

        // Send to debug-enabled players
        String debugMessage = ChatColor.GRAY + "[" + ChatColor.YELLOW + "Debug" + 
                            ChatColor.GRAY + "] " + ChatColor.WHITE + message;
        
        for (UUID playerId : debugPlayers) {
            Player player = plugin.getServer().getPlayer(playerId);
            if (player != null && player.hasPermission("msnairdrop.admin")) {
                player.sendMessage(debugMessage);
            }
        }
    }

    public void debugLocation(String context, Object... args) {
        if (!debugMode) {
            return;
        }

        StringBuilder message = new StringBuilder(context);
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                message.append("\n  ").append(args[i]).append(": ").append(args[i + 1]);
            }
        }
        debug(message.toString());
    }

    public boolean isDebugEnabled(Player player) {
        return debugPlayers.contains(player.getUniqueId());
    }
}