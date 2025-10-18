package com.msncakma.msnairdrop.utils;

import com.msncakma.msnairdrop.MsnAirDrop;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    private final MsnAirDrop plugin;
    private final BukkitAudiences adventure;
    private final MiniMessage miniMessage;

    public MessageManager(MsnAirDrop plugin) {
        this.plugin = plugin;
        this.adventure = BukkitAudiences.create(plugin);
        this.miniMessage = MiniMessage.miniMessage();
    }

    public void sendMessage(Player player, String message) {
        adventure.player(player).sendMessage(miniMessage.deserialize(message));
    }

    public void sendMessage(Player player, String message, String... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be in pairs!");
        }

        List<TagResolver> resolvers = new ArrayList<>();
        for (int i = 0; i < placeholders.length; i += 2) {
            resolvers.add(Placeholder.parsed(placeholders[i], placeholders[i + 1]));
        }

        Component component = miniMessage.deserialize(message, TagResolver.resolver(resolvers));
        adventure.player(player).sendMessage(component);
    }

    public void broadcastMessage(String message) {
        Component component = miniMessage.deserialize(message);
        adventure.all().sendMessage(component);
    }

    public void broadcastMessage(String message, String... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be in pairs!");
        }

        List<TagResolver> resolvers = new ArrayList<>();
        for (int i = 0; i < placeholders.length; i += 2) {
            resolvers.add(Placeholder.parsed(placeholders[i], placeholders[i + 1]));
        }

        Component component = miniMessage.deserialize(message, TagResolver.resolver(resolvers));
        adventure.all().sendMessage(component);
    }

    public String parseMessage(String message) {
        return miniMessage.serialize(miniMessage.deserialize(message));
    }

    public void close() {
        if (this.adventure != null) {
            this.adventure.close();
        }
    }
}