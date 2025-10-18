package com.msncakma.msnairdrop.drops;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemBuilder {
    public static ItemStack fromConfig(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        // Get basic item info
        String materialName = section.getString("material");
        if (materialName == null) {
            return null;
        }

        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }

        int amount = section.getInt("amount", 1);
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        // Set display name
        String displayName = section.getString("name");
        if (displayName != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        }

        // Set lore
        List<String> lore = section.getStringList("lore");
        if (!lore.isEmpty()) {
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(coloredLore);
        }

        // Add enchantments
        ConfigurationSection enchants = section.getConfigurationSection("enchantments");
        if (enchants != null) {
            for (String enchantName : enchants.getKeys(false)) {
                try {
                    Enchantment enchantment = Enchantment.getByName(enchantName.toUpperCase());
                    if (enchantment != null) {
                        int level = enchants.getInt(enchantName);
                        meta.addEnchant(enchantment, level, true);
                    }
                } catch (IllegalArgumentException ignored) {
                    // Invalid enchantment name, skip it
                }
            }
        }

        // Set item flags
        List<String> flags = section.getStringList("flags");
        for (String flag : flags) {
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(flag.toUpperCase());
                meta.addItemFlags(itemFlag);
            } catch (IllegalArgumentException ignored) {
                // Invalid flag, skip it
            }
        }

        item.setItemMeta(meta);
        return item;
    }
}