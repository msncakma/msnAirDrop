package com.msncakma.msnairdrop.util;

import com.msncakma.msnairdrop.database.PlayerStats;
import com.msncakma.msnairdrop.database.ZoneStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.text.DecimalFormat;
import java.util.UUID;

public class StatsFormatter {
    private final MiniMessage miniMessage;
    private final DecimalFormat df;

    public StatsFormatter() {
        this.miniMessage = MiniMessage.miniMessage();
        this.df = new DecimalFormat("#,##0.##");
    }

    public Component formatPlayerStats(String playerName, PlayerStats playerStats, ZoneStats zoneStats, UUID playerUuid) {
        double kdRatio = zoneStats.getKDRatio(playerUuid);
        int kills = zoneStats.getZoneKills(playerUuid);
        int deaths = zoneStats.getZoneDeaths(playerUuid);
        int gapples = zoneStats.getGoldenApplesUsed(playerUuid);
        int damageDealt = zoneStats.getDamageDealt(playerUuid);
        int damageTaken = zoneStats.getDamageTaken(playerUuid);
        int fireworks = zoneStats.getFireworksUsed(playerUuid);

        TagResolver.Builder placeholders = TagResolver.builder()
            .resolver(Placeholder.parsed("player", playerName))
            .resolver(Placeholder.parsed("events_won", String.valueOf(playerStats.getEventsWon())))
            .resolver(Placeholder.parsed("events_total", String.valueOf(playerStats.getEventsParticipated())))
            .resolver(Placeholder.parsed("win_rate", df.format(playerStats.getWinRate())))
            .resolver(Placeholder.parsed("kills", String.valueOf(kills)))
            .resolver(Placeholder.parsed("deaths", String.valueOf(deaths)))
            .resolver(Placeholder.parsed("kd_ratio", df.format(kdRatio)))
            .resolver(Placeholder.parsed("gapples", String.valueOf(gapples)))
            .resolver(Placeholder.parsed("damage_dealt", df.format(damageDealt)))
            .resolver(Placeholder.parsed("damage_taken", df.format(damageTaken)))
            .resolver(Placeholder.parsed("fireworks", String.valueOf(fireworks)));

        String template = """
            <gradient:#FFD700:#FFA500>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</gradient>
            <gradient:#FFD700:#FFA500>⚔ AirDrop Stats: <player></gradient>
            
            <yellow>Event Stats:</yellow>
            <gray>• Events Won:</gray> <gold><events_won></gold>
            <gray>• Total Participated:</gray> <gold><events_total></gold>
            <gray>• Win Rate:</gray> <gold><win_rate>%</gold>
            
            <yellow>Combat Stats:</yellow>
            <gray>• Kills:</gray> <gold><kills></gold>
            <gray>• Deaths:</gray> <gold><deaths></gold>
            <gray>• K/D Ratio:</gray> <gold><kd_ratio></gold>
            
            <yellow>Items Usage:</yellow>
            <gray>• Golden Apples:</gray> <gold><gapples></gold>
            <gray>• Damage Dealt:</gray> <gold><damage_dealt></gold>
            <gray>• Damage Taken:</gray> <gold><damage_taken></gold>
            <gray>• Fireworks:</gray> <gold><fireworks></gold>
            
            <gradient:#FFD700:#FFA500>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬</gradient>
            """;

        return miniMessage.deserialize(template, placeholders.build())
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public Component formatPlayerNotFound(String playerName) {
        return miniMessage.deserialize(
            "<red>Player <gold>" + playerName + "</gold> not found or has no statistics.</red>",
            TagResolver.empty()
        ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}