package com.msncakma.msnairdrop.util;

import com.msncakma.msnairdrop.database.PlayerStats;
import com.msncakma.msnairdrop.database.ZoneStats;
import com.msncakma.msnairdrop.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.text.DecimalFormat;
import java.util.UUID;

public class StatsFormatter {
    private final DecimalFormat df;

    public StatsFormatter() {
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

        String template = """
            <gold>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
            <gold>⚔ AirDrop Stats: <player>
            
            <yellow><bold>Event Stats:</bold>
            <gray>• Events Won: <gold><events_won>
            <gray>• Total Participated: <gold><events_total>
            <gray>• Win Rate: <gold><win_rate>%</gold>
            
            <yellow><bold>Combat Stats:</bold>
            <gray>• Kills: <gold><kills>
            <gray>• Deaths: <gold><deaths>
            <gray>• K/D Ratio: <gold><kd_ratio>
            
            <yellow><bold>Items Usage:</bold>
            <gray>• Golden Apples: <gold><gapples>
            <gray>• Damage Dealt: <gold><damage_dealt>
            <gray>• Damage Taken: <gold><damage_taken>
            <gray>• Fireworks: <gold><fireworks>
            
            <gold>▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬
            """;

        return MessageUtil.format(template,
            "player", playerName,
            "events_won", String.valueOf(playerStats.getEventsWon()),
            "events_total", String.valueOf(playerStats.getEventsParticipated()),
            "win_rate", df.format(playerStats.getWinRate()),
            "kills", String.valueOf(kills),
            "deaths", String.valueOf(deaths),
            "kd_ratio", df.format(kdRatio),
            "gapples", String.valueOf(gapples),
            "damage_dealt", df.format(damageDealt),
            "damage_taken", df.format(damageTaken),
            "fireworks", String.valueOf(fireworks)
        );
    }

    public Component formatPlayerNotFound(String playerName) {
        return MessageUtil.format(
            "<red><bold>Player <gold><player></gold> not found or has no statistics.",
            "player", playerName
        );
    }
}