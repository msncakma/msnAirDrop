package com.msncakma.msnairdrop.commands;

import com.msncakma.msnairdrop.MsnAirDrop;
import com.msncakma.msnairdrop.database.DatabaseQueries;
import com.msncakma.msnairdrop.database.PlayerStats;
import com.msncakma.msnairdrop.database.ZoneStats;
import com.msncakma.msnairdrop.util.StatsFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AirDropStatsCommand implements CommandExecutor, TabCompleter {
    private final MsnAirDrop plugin;
    private final DatabaseQueries dbQueries;
    private final ZoneStats zoneStats;
    private final StatsFormatter formatter;

    public AirDropStatsCommand(MsnAirDrop plugin, DatabaseQueries dbQueries, ZoneStats zoneStats) {
        this.plugin = plugin;
        this.dbQueries = dbQueries;
        this.zoneStats = zoneStats;
        this.formatter = new StatsFormatter();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, String[] args) {
        
        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cPlease specify a player name!");
                return true;
            }
            // If no argument is provided and sender is a player, show their own stats
            showStats(sender, ((Player) sender).getName());
            return true;
        }

        showStats(sender, args[0]);
        return true;
    }

    private void showStats(CommandSender sender, String playerName) {
        plugin.getTaskScheduler().runTaskAsync(() -> {
            // Try to get UUID from player name
            UUID targetUuid = null;
            Player targetPlayer = Bukkit.getPlayer(playerName);
            
            if (targetPlayer != null) {
                targetUuid = targetPlayer.getUniqueId();
            } else {
                // Try to get from offline player
                @SuppressWarnings("deprecation")
                Player offlinePlayer = Bukkit.getOfflinePlayer(playerName).getPlayer();
                if (offlinePlayer != null) {
                    targetUuid = offlinePlayer.getUniqueId();
                }
            }

            if (targetUuid == null) {
                Bukkit.getScheduler().runTask(plugin, () -> 
                    sender.sendMessage(formatter.formatPlayerNotFound(playerName).toString()));
                return;
            }

            final Optional<PlayerStats> stats = dbQueries.getPlayerStats(targetUuid);
            if (stats.isEmpty()) {
                final String finalPlayerName = playerName;
                Bukkit.getScheduler().runTask(plugin, () -> 
                    sender.sendMessage(formatter.formatPlayerNotFound(finalPlayerName).toString()));
                return;
            }

            final PlayerStats finalStats = stats.get();
            final UUID finalTargetUuid = targetUuid;
            final String finalPlayerName = playerName;
            Bukkit.getScheduler().runTask(plugin, () -> 
                sender.sendMessage(formatter.formatPlayerStats(finalPlayerName, finalStats, zoneStats, finalTargetUuid).toString()));
        });
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String label, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String partialName = args[0].toLowerCase();
            
            // Add online players that match the partial name
            for (Player player : Bukkit.getOnlinePlayers()) {
                String name = player.getName();
                if (name.toLowerCase().startsWith(partialName)) {
                    completions.add(name);
                }
            }
            
            return completions;
        }
        
        return new ArrayList<>();
    }
}