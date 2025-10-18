package com.msncakma.msnairdrop.drops;

import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DropManager {
    private final MsnAirDrop plugin;
    private final Map<String, Drop> drops;
    private final Random random;
    private final File dropsFolder;

    public DropManager(MsnAirDrop plugin) {
        this.plugin = plugin;
        this.drops = new HashMap<>();
        this.random = new Random();
        this.dropsFolder = new File(plugin.getDataFolder(), "drops");
        loadDrops();
    }

    private void loadDrops() {
        if (!dropsFolder.exists()) {
            dropsFolder.mkdirs();
            createDefaultDrops();
        }

        for (File file : dropsFolder.listFiles((dir, name) -> name.endsWith(".yml"))) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String dropName = file.getName().replace(".yml", "");

            Drop drop = Drop.fromConfig(dropName, config);
            if (drop != null) {
                drops.put(dropName, drop);
                plugin.getLogger().info("Loaded drop: " + dropName);
            }
        }

        if (drops.isEmpty()) {
            plugin.getLogger().warning("No drops were loaded!");
        }
    }

    private void createDefaultDrops() {
        createDefaultDrop("common");
        createDefaultDrop("rare");
        createDefaultDrop("legendary");
    }

    private void createDefaultDrop(String name) {
        File file = new File(dropsFolder, name + ".yml");
        if (!file.exists()) {
            plugin.saveResource("drops/" + name + ".yml", false);
        }
    }

    public Drop getRandomDrop() {
        if (drops.isEmpty()) {
            return null;
        }

        double totalChance = drops.values().stream()
            .mapToDouble(Drop::getChance)
            .sum();

        double randomValue = random.nextDouble() * totalChance;
        double currentSum = 0;

        for (Drop drop : drops.values()) {
            currentSum += drop.getChance();
            if (randomValue <= currentSum) {
                return drop;
            }
        }

        return drops.values().iterator().next(); // Fallback to first drop
    }

    public void fillChest(Inventory inventory) {
        Drop drop = getRandomDrop();
        if (drop != null) {
            drop.fillInventory(inventory);
        }
    }

    public void reload() {
        drops.clear();
        loadDrops();
    }
}