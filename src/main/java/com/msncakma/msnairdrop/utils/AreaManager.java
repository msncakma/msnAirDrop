package com.msncakma.msnairdrop.utils;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AreaManager {
    private final Map<UUID, Location> pos1Map = new HashMap<>();
    private final Map<UUID, Location> pos2Map = new HashMap<>();

    public void setPos1(Player player, Location location) {
        pos1Map.put(player.getUniqueId(), location.clone());
    }

    public void setPos2(Player player, Location location) {
        pos2Map.put(player.getUniqueId(), location.clone());
    }

    public Location getPos1(Player player) {
        return pos1Map.get(player.getUniqueId());
    }

    public Location getPos2(Player player) {
        return pos2Map.get(player.getUniqueId());
    }

    public boolean hasCompleteSelection(Player player) {
        UUID playerId = player.getUniqueId();
        return pos1Map.containsKey(playerId) && pos2Map.containsKey(playerId);
    }

    public void clearSelection(Player player) {
        UUID playerId = player.getUniqueId();
        pos1Map.remove(playerId);
        pos2Map.remove(playerId);
    }

    public void saveToConfig(ConfigurationSection config, Location pos1, Location pos2) {
        if (pos1 != null) {
            config.set("pos1.world", pos1.getWorld().getName());
            config.set("pos1.x", pos1.getBlockX());
            config.set("pos1.y", pos1.getBlockY());
            config.set("pos1.z", pos1.getBlockZ());
        }

        if (pos2 != null) {
            config.set("pos2.world", pos2.getWorld().getName());
            config.set("pos2.x", pos2.getBlockX());
            config.set("pos2.y", pos2.getBlockY());
            config.set("pos2.z", pos2.getBlockZ());
        }
    }
}