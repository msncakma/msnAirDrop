package com.msncakma.msnairdrop.rewards;

import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RewardManager {
    private final MsnAirDrop plugin;
    private final File rewardsFolder;
    private final YamlConfiguration activeRewards;
    private final File activeRewardsFile;

    public RewardManager(MsnAirDrop plugin) {
        this.plugin = plugin;
        this.rewardsFolder = new File(plugin.getDataFolder(), "rewards");
        this.rewardsFolder.mkdirs();
        
        this.activeRewardsFile = new File(rewardsFolder, "active_rewards.yml");
        this.activeRewards = YamlConfiguration.loadConfiguration(activeRewardsFile);
        
        // Create default rewards if none exist
        if (!activeRewardsFile.exists()) {
            saveDefaultRewards();
        }
    }

    private void saveDefaultRewards() {
        activeRewards.set("rewards.example.commands", List.of(
            "eco give %player% 1000000",
            "give %player% diamond 64"
        ));
        saveActiveRewards();
    }

    public void addItemReward(String name, ItemStack item, Player creator) {
        String path = "rewards." + name;
        activeRewards.set(path + ".item", item);
        activeRewards.set(path + ".creator", creator.getUniqueId().toString());
        activeRewards.set(path + ".created", System.currentTimeMillis());
        saveActiveRewards();
        
        plugin.getDebugManager().debug("Added new item reward: " + name + " by " + creator.getName());
    }

    public void addCommandReward(String name, List<String> commands, Player creator) {
        String path = "rewards." + name;
        activeRewards.set(path + ".commands", commands);
        activeRewards.set(path + ".creator", creator.getUniqueId().toString());
        activeRewards.set(path + ".created", System.currentTimeMillis());
        saveActiveRewards();
        
        plugin.getDebugManager().debug("Added new command reward: " + name + " by " + creator.getName());
    }

    public void removeReward(String name) {
        activeRewards.set("rewards." + name, null);
        saveActiveRewards();
        
        plugin.getDebugManager().debug("Removed reward: " + name);
    }

    public List<String> listRewards() {
        List<String> rewards = new ArrayList<>();
        ConfigurationSection rewardsSection = activeRewards.getConfigurationSection("rewards");
        if (rewardsSection != null) {
            rewards.addAll(rewardsSection.getKeys(false));
        }
        return rewards;
    }

    public void giveReward(String name, Player player) {
        String path = "rewards." + name;
        if (!activeRewards.contains(path)) {
            return;
        }

        // Give item rewards
        ItemStack item = activeRewards.getItemStack(path + ".item");
        if (item != null) {
            player.getInventory().addItem(item.clone());
        }

        // Execute commands
        List<String> commands = activeRewards.getStringList(path + ".commands");
        for (String command : commands) {
            String processedCommand = command.replace("%player%", player.getName());
            plugin.getServer().dispatchCommand(
                plugin.getServer().getConsoleSender(),
                processedCommand
            );
        }

        plugin.getDebugManager().debug("Given reward " + name + " to " + player.getName());
    }

    public YamlConfiguration getActiveRewards() {
        return activeRewards;
    }

    private void saveActiveRewards() {
        try {
            activeRewards.save(activeRewardsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save rewards: " + e.getMessage());
        }
    }

    public void reload() {
        try {
            activeRewards.load(activeRewardsFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Could not reload rewards: " + e.getMessage());
        }
    }
}