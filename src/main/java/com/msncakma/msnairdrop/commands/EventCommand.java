package com.msncakma.msnairdrop.commands;

import com.msncakma.msnairdrop.MsnAirDrop;
import com.msncakma.msnairdrop.events.EventManager;
import com.msncakma.msnairdrop.utils.AreaManager;
import com.msncakma.msnairdrop.utils.LocationUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventCommand implements CommandExecutor {
    private final MsnAirDrop plugin;
    private final EventManager eventManager;
    private final AreaManager areaManager;

    public EventCommand(MsnAirDrop plugin, EventManager eventManager, AreaManager areaManager) {
        this.plugin = plugin;
        this.eventManager = eventManager;
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

        if (args.length < 1) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                handleStart(player, args.length > 1 && args[1].equalsIgnoreCase("test"));
                break;
            case "stop":
                handleStop(player);
                break;
            case "locate":
                handleLocate(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleStart(Player player, boolean isTest) {
        String prefix = plugin.getLanguageManager().getMessage("prefix");

        if (!areaManager.hasCompleteSelection(player)) {
            player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("errors.no-area-set"));
            return;
        }

        if (eventManager.isEventActive()) {
            player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("errors.event-in-progress"));
            return;
        }

        boolean started = eventManager.startEvent(
            areaManager.getPos1(player),
            areaManager.getPos2(player),
            isTest
        );

        if (started) {
            player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("admin.event-started"));
        }
    }

    private void handleStop(Player player) {
        String prefix = plugin.getLanguageManager().getMessage("prefix");

        if (!eventManager.isEventActive()) {
            player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("errors.no-event-active"));
            return;
        }

        eventManager.stopEvent();
        player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("admin.event-stopped"));
    }

    private void handleLocate(Player player) {
        String prefix = plugin.getLanguageManager().getMessage("prefix");

        if (!eventManager.isEventActive()) {
            player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("errors.no-event-active"));
            return;
        }

        String location = LocationUtils.formatLocation(eventManager.getDropLocation());
        player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("admin.drop-location", 
            "location", location));
    }

    private void sendHelp(Player player) {
        String prefix = plugin.getLanguageManager().getMessage("prefix");
        player.sendMessage(prefix + " §6=== AirDrop Commands ===");
        player.sendMessage("§e/msnairdrop:pos1 §7- Set first position");
        player.sendMessage("§e/msnairdrop:pos2 §7- Set second position");
        player.sendMessage("§e/msnairdrop event start [test] §7- Start an event");
        player.sendMessage("§e/msnairdrop event stop §7- Stop current event");
        player.sendMessage("§e/msnairdrop event locate §7- Get drop location");
    }
}