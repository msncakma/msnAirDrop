package com.msncakma.msnairdrop.commands;

import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StatsCommand implements CommandExecutor, TabCompleter {
    private final MsnAirDrop plugin;

    public StatsCommand(MsnAirDrop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("msnairdrop.stats")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("no-permission"));
            return true;
        }

        Player target;
        if (args.length > 0) {
            target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(plugin.getLanguageManager().getMessage("player-not-found"));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(plugin.getLanguageManager().getMessage("console-specify-player"));
            return true;
        }

        // TODO: Implement stats display
        sender.sendMessage(plugin.getLanguageManager().getMessage("stats-header"));
        // Add stats display implementation here
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("msnairdrop.stats")) {
            List<String> players = new ArrayList<>();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    players.add(player.getName());
                }
            }
            return players;
        }
        return new ArrayList<>();
    }
}