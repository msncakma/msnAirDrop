package com.msncakma.msnairdrop.events;

import com.msncakma.msnairdrop.MsnAirDrop;
import com.msncakma.msnairdrop.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
public class AirDropEvent {
    private final MsnAirDrop plugin;
    private final Location pos1;
    private final Location pos2;
    private Location dropLocation;
    private boolean isActive;
    private boolean isAccessible;
    private boolean isTestMode;
    private Material dropBlock;

    public AirDropEvent(MsnAirDrop plugin, Location pos1, Location pos2, boolean isTestMode) {
        this.plugin = plugin;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.isActive = false;
        this.isAccessible = false;
        this.isTestMode = isTestMode;
        this.dropBlock = Material.valueOf(plugin.getConfig().getString("event.block-type", "CHEST"));
    }

    public void start() {
        if (isActive) {
            return;
        }

        isActive = true;
        dropLocation = LocationUtils.getRandomLocation(pos1, pos2);
        
        if (dropLocation == null) {
            cancel();
            return;
        }

        // Announce the event area
        announceEventStart();

        // Start countdown
        int announceDelay = plugin.getConfig().getInt("event.announce-delay", 300);
        plugin.getTaskScheduler().runTaskLater(() -> spawnDrop(), announceDelay * 20L);
    }

    private void spawnDrop() {
        plugin.getTaskScheduler().runRegionTask(dropLocation, () -> {
            Block block = dropLocation.getBlock();
            block.setType(dropBlock);

            if (dropBlock == Material.CHEST) {
                Chest chest = (Chest) block.getState();
                plugin.getDropManager().fillChest(chest.getInventory());
            }
        });

        // Announce drop location
        String locationMsg = plugin.getLanguageManager().getMessage("event.started", 
            "coordinates", LocationUtils.formatLocation(dropLocation));
        plugin.getServer().broadcastMessage(locationMsg);

        // Start access countdown
        int accessDelay = plugin.getConfig().getInt("event.access-delay", 1800);
        plugin.getTaskScheduler().runTaskLater(() -> makeAccessible(), accessDelay * 20L);
    }

    private void makeAccessible() {
        isAccessible = true;
        String accessMsg = plugin.getLanguageManager().getMessage("event.access-granted");
        plugin.getServer().broadcastMessage(accessMsg);
    }

    public void cancel() {
        if (dropLocation != null && dropLocation.getBlock().getType() == dropBlock) {
            plugin.getTaskScheduler().runRegionTask(dropLocation, () -> 
                dropLocation.getBlock().setType(Material.AIR));
        }
        isActive = false;
        isAccessible = false;
    }

    private void announceEventStart() {
        String area = String.format("%s to %s", 
            LocationUtils.formatLocation(pos1),
            LocationUtils.formatLocation(pos2));
        
        String message = plugin.getLanguageManager().getMessage(
            isTestMode ? "event.test-start" : "event.start", 
            "area", area
        );

        if (isTestMode) {
            // Only send to players with test permission
            plugin.getServer().getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("msnairdrop.admin"))
                .forEach(p -> p.sendMessage(message));
            
            plugin.getDebugManager().debug("Test event started in area: " + area);
        } else {
            plugin.getServer().broadcastMessage(message);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isAccessible() {
        return isAccessible;
    }

    public Location getDropLocation() {
        return dropLocation;
    }

    public void handleWinner(Player player) {
        if (!isActive || !isAccessible) {
            return;
        }

        String winMessage = plugin.getLanguageManager().getMessage("event.winner", "player", player.getName());
        plugin.getServer().broadcastMessage(winMessage);
        
        // Clean up
        cancel();
    }
}