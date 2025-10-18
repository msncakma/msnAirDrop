package com.msncakma.msnairdrop.utils;

import com.msncakma.msnairdrop.MsnAirDrop;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class MessageManager {
    private final MsnAirDrop plugin;

    public MessageManager(MsnAirDrop plugin) {
        this.plugin = plugin;
    }

    public void sendMessage(Player player, String message) {
        player.sendMessage(MessageUtil.color(message));
    }

    public void sendMessage(Player player, String message, String... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be in pairs!");
        }

        player.sendMessage(MessageUtil.format(message, placeholders));
    }

    public void broadcastMessage(String message) {
        Component colored = MessageUtil.color(message);
        plugin.getServer().sendMessage(colored);
    }

    public void broadcastMessage(String message, String... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be in pairs!");
        }

        Component formatted = MessageUtil.format(message, placeholders);
        plugin.getServer().sendMessage(formatted);
    }

    public Component parseMessage(String message) {
        return MessageUtil.color(message);
    }
}