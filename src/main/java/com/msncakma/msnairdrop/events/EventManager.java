package com.msncakma.msnairdrop.events;

import com.msncakma.msnairdrop.MsnAirDrop;
import com.msncakma.msnairdrop.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventManager implements Listener {
    private final MsnAirDrop plugin;
    private AirDropEvent currentEvent;

    public EventManager(MsnAirDrop plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public boolean startEvent(Location pos1, Location pos2, boolean isTest) {
        if (currentEvent != null && currentEvent.isActive()) {
            return false;
        }

        plugin.getDebugManager().debug("Starting " + (isTest ? "test " : "") + "event");
        plugin.getDebugManager().debugLocation("Event area",
            "pos1", LocationUtils.formatLocation(pos1),
            "pos2", LocationUtils.formatLocation(pos2),
            "test_mode", isTest);

        currentEvent = new AirDropEvent(plugin, pos1, pos2, isTest);
        currentEvent.start();
        return true;
    }

    public void stopEvent() {
        if (currentEvent != null) {
            currentEvent.cancel();
            currentEvent = null;
        }
    }

    public boolean isEventActive() {
        return currentEvent != null && currentEvent.isActive();
    }

    public Location getDropLocation() {
        return currentEvent != null ? currentEvent.getDropLocation() : null;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (currentEvent != null && !currentEvent.isAccessible()) {
            Location broken = event.getBlock().getLocation();
            Location drop = currentEvent.getDropLocation();
            
            if (drop != null && broken.equals(drop)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.getLanguageManager().getMessage("errors.not-accessible"));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (currentEvent != null && currentEvent.isAccessible()) {
            Location clicked = event.getClickedBlock().getLocation();
            Location drop = currentEvent.getDropLocation();
            
            if (drop != null && clicked.equals(drop)) {
                currentEvent.handleWinner(event.getPlayer());
            }
        }
    }
}