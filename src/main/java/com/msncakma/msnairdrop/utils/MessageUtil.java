package com.msncakma.msnairdrop.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageUtil {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component color(String message) {
        if (message == null) return Component.empty();
        return miniMessage.deserialize(message);
    }

    public static Component format(String message, Map<String, String> placeholders) {
        if (message == null) return Component.empty();
        
        List<TagResolver> resolvers = new ArrayList<>();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            resolvers.add(Placeholder.parsed(entry.getKey(), entry.getValue()));
        }
        
        return miniMessage.deserialize(message, TagResolver.resolver(resolvers));
    }

    public static Component format(String message, String... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be key-value pairs");
        }
        
        List<TagResolver> resolvers = new ArrayList<>();
        for (int i = 0; i < placeholders.length; i += 2) {
            resolvers.add(Placeholder.parsed(placeholders[i], placeholders[i + 1]));
        }
        
        return miniMessage.deserialize(message, TagResolver.resolver(resolvers));
    }

    public static String stripColor(String message) {
        if (message == null) return "";
        return miniMessage.stripTags(message);
    }

    public static String legacyString(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}