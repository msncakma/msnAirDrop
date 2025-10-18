package com.msncakma.msnairdrop.drops;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Drop {
    private final String name;
    private final List<ItemStack> items;
    private final double chance;
    private final List<String> commands;
    private static final Random random = new Random();

    public Drop(String name, List<ItemStack> items, double chance, List<String> commands) {
        this.name = name;
        this.items = items;
        this.chance = chance;
        this.commands = commands != null ? commands : new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public double getChance() {
        return chance;
    }

    public void fillInventory(Inventory inventory) {
        if (items == null || items.isEmpty()) {
            return;
        }

        List<Integer> emptySlots = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                emptySlots.add(i);
            }
        }

        for (ItemStack item : items) {
            if (emptySlots.isEmpty()) {
                break;
            }

            int randomSlot = emptySlots.remove(random.nextInt(emptySlots.size()));
            inventory.setItem(randomSlot, item.clone());
        }
    }

    public void executeCommands(Player player) {
        if (commands.isEmpty()) {
            return;
        }

        for (String command : commands) {
            String processedCommand = command.replace("%player%", player.getName());
            player.getServer().dispatchCommand(
                player.getServer().getConsoleSender(),
                processedCommand
            );
        }
    }

    public static Drop fromConfig(String name, ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        List<ItemStack> items = new ArrayList<>();
        ConfigurationSection itemsSection = section.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                ItemStack item = ItemBuilder.fromConfig(itemSection);
                if (item != null) {
                    items.add(item);
                }
            }
        }

        double chance = section.getDouble("chance", 100.0);
        List<String> commands = section.getStringList("commands");

        return new Drop(name, items, chance, commands);
    }
}