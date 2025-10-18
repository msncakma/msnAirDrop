package com.msncakma.msnairdrop.placeholder;

import com.msncakma.msnairdrop.MsnAirDrop;
import com.msncakma.msnairdrop.database.ZoneStats;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class AirDropPlaceholders extends PlaceholderExpansion {
    private final MsnAirDrop plugin;
    private final ZoneStats zoneStats;
    private final DecimalFormat df;

    public AirDropPlaceholders(MsnAirDrop plugin, ZoneStats zoneStats) {
        this.plugin = plugin;
        this.zoneStats = zoneStats;
        this.df = new DecimalFormat("#.##");
    }

    @Override
    public String getIdentifier() {
        return "msnairdrop";
    }

    @Override
    public String getAuthor() {
        return "msncakma";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return "";
        }

        return switch (params.toLowerCase()) {
            case "zone_kills" -> String.valueOf(zoneStats.getZoneKills(player.getUniqueId()));
            case "zone_deaths" -> String.valueOf(zoneStats.getZoneDeaths(player.getUniqueId()));
            case "kd_ratio" -> df.format(zoneStats.getKDRatio(player.getUniqueId()));
            case "golden_apples" -> String.valueOf(zoneStats.getGoldenApplesUsed(player.getUniqueId()));
            case "damage_dealt" -> String.valueOf(zoneStats.getDamageDealt(player.getUniqueId()));
            case "damage_taken" -> String.valueOf(zoneStats.getDamageTaken(player.getUniqueId()));
            case "fireworks" -> String.valueOf(zoneStats.getFireworksUsed(player.getUniqueId()));
            default -> null;
        };
    }
}