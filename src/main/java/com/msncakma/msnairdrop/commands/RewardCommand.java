package com.msncakma.msnairdrop.commands;

import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RewardCommand implements CommandExecutor {
    private final MsnAirDrop plugin;

    public RewardCommand(MsnAirDrop plugin) {
        this.plugin = plugin;
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
            case "add":
                handleAdd(player, args);
                break;
            case "additem":
                handleAddItem(player, args);
                break;
            case "remove":
                handleRemove(player, args);
                break;
            case "list":
                handleList(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + " " +
                             plugin.getLanguageManager().getMessage("commands.reward.usage-add"));
            return;
        }

        String name = args[1];
        List<String> commands = new ArrayList<>(Arrays.asList(args).subList(2, args.length));
        
        plugin.getRewardManager().addCommandReward(name, commands, player);
        player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + " " +
                          plugin.getLanguageManager().getMessage("admin.reward-added", 
                          "name", name));
    }

    private void handleAddItem(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + " " +
                             plugin.getLanguageManager().getMessage("commands.reward.usage-additem"));
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + " " +
                             plugin.getLanguageManager().getMessage("errors.no-item-in-hand"));
            return;
        }

        String name = args[1];
        plugin.getRewardManager().addItemReward(name, item.clone(), player);
        player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + " " +
                          plugin.getLanguageManager().getMessage("admin.reward-item-added", 
                          "name", name));
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + " " +
                             plugin.getLanguageManager().getMessage("commands.reward.usage-remove"));
            return;
        }

        String name = args[1];
        plugin.getRewardManager().removeReward(name);
        player.sendMessage(plugin.getLanguageManager().getMessage("prefix") + " " +
                          plugin.getLanguageManager().getMessage("admin.reward-removed", 
                          "name", name));
    }

    private void handleList(Player player) {
        List<String> rewards = plugin.getRewardManager().listRewards();
        String prefix = plugin.getLanguageManager().getMessage("prefix");
        
        player.sendMessage(prefix + " §6=== Active Rewards ===");
        if (rewards.isEmpty()) {
            player.sendMessage("§7No rewards configured");
        } else {
            rewards.forEach(name -> {
                player.sendMessage("§e- " + name);
            });
        }
    }

    private void sendHelp(Player player) {
        String prefix = plugin.getLanguageManager().getMessage("prefix");
        player.sendMessage(prefix + " §6=== Reward Commands ===");
        player.sendMessage("§e/msnairdrop reward add <name> <command...> §7- Add a command reward");
        player.sendMessage("§e/msnairdrop reward additem <name> §7- Add held item as reward");
        player.sendMessage("§e/msnairdrop reward remove <name> §7- Remove a reward");
        player.sendMessage("§e/msnairdrop reward list §7- List all rewards");
    }
}