package com.msncakma.msnairdrop.commands;

import com.msncakma.msnairdrop.MsnAirDrop;
import com.msncakma.msnairdrop.utils.AreaManager;
import com.msncakma.msnairdrop.utils.LocationUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PositionCommand implements CommandExecutor {
    private final MsnAirDrop plugin;
    private final AreaManager areaManager;

    public PositionCommand(MsnAirDrop plugin, AreaManager areaManager) {
        this.plugin = plugin;
        this.areaManager = areaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("errors.player-only"));
            return true;
        }

        Player player = (Player) sender;
        String prefix = plugin.getLanguageManager().getMessage("prefix");

        if (!player.hasPermission("msnairdrop.admin")) {
            player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("commands.no-permission"));
            return true;
        }

        if (args.length == 0 || (!args[0].equalsIgnoreCase("pos1") && !args[0].equalsIgnoreCase("pos2"))) {
            player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("admin.pos-usage"));
            return true;
        }

        if (args[0].equalsIgnoreCase("pos1")) {
            areaManager.setPos1(player, player.getLocation());
            player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("admin.pos1-set", 
                "pos", LocationUtils.formatLocation(player.getLocation())));
            return true;
        }

        if (args[0].equalsIgnoreCase("pos2")) {
            areaManager.setPos2(player, player.getLocation());
            player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("admin.pos2-set", 
                "pos", LocationUtils.formatLocation(player.getLocation())));

            if (areaManager.hasCompleteSelection(player)) {
                player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("admin.area-set"));
            }
            return true;
        }

        return false;
    }
}