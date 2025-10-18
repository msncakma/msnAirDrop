package com.msncakma.msnairdrop.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Random;

public class LocationUtils {
    private static final Random random = new Random();

    public static Location getRandomLocation(Location pos1, Location pos2) {
        if (pos1 == null || pos2 == null || pos1.getWorld() != pos2.getWorld()) {
            return null;
        }

        World world = pos1.getWorld();
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        int x = minX + random.nextInt(maxX - minX + 1);
        int z = minZ + random.nextInt(maxZ - minZ + 1);
        int y = getHighestSafeBlock(world, x, z);

        return new Location(world, x + 0.5, y, z + 0.5);
    }

    private static int getHighestSafeBlock(World world, int x, int z) {
        int y = world.getHighestBlockYAt(x, z);
        Block block = world.getBlockAt(x, y, z);
        
        // Check if the block is safe to spawn on
        while (y > 0 && !isSafeBlock(block)) {
            y--;
            block = world.getBlockAt(x, y, z);
        }
        
        return y + 1;
    }

    private static boolean isSafeBlock(Block block) {
        return block.getType().isSolid() && !block.getType().name().contains("LEAVES");
    }

    public static String formatLocation(Location loc) {
        if (loc == null) return "Invalid Location";
        return String.format("X: %d, Y: %d, Z: %d", 
            loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}