package com.msncakma.msnairdrop.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public class UpdateChecker implements Listener {
    private final MsnAirDrop plugin;
    private final String currentVersion;
    private String latestVersion;
    private boolean updateAvailable = false;

    private static final String GITHUB_API_URL = "https://api.github.com/repos/msncakma/MsnAirDrop/releases/latest";

    public UpdateChecker(MsnAirDrop plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        checkForUpdates();
    }

    private void checkForUpdates() {
        if (!plugin.getConfig().getBoolean("settings.check-updates", true)) {
            return;
        }

        // Use a standard Java thread for the update check
        Thread updateThread = new Thread(() -> {
            try {
                URL url = new URL(GITHUB_API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                    );
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                    latestVersion = json.get("tag_name").getAsString().replace("v", "");

                    if (!currentVersion.equals(latestVersion)) {
                        updateAvailable = true;
                        plugin.getLogger().info("New version available: " + latestVersion);
                        plugin.getLogger().info("Download it from: https://github.com/msncakma/MsnAirDrop/releases");
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to check for updates: " + e.getMessage());
            }
        });
        updateThread.setDaemon(true); // Don't prevent JVM shutdown
        updateThread.start();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (updateAvailable && player.hasPermission("msnairdrop.admin")) {
            // Using Folia's region-based scheduling
            io.papermc.paper.threadedregions.scheduler.ScheduledTask task = 
                player.getScheduler().runDelayed(plugin, (scheduledTask) -> {
                    String prefix = plugin.getLanguageManager().getMessage("prefix");
                    player.sendMessage(prefix + " " + plugin.getLanguageManager().getMessage("admin.update-available",
                        "current", currentVersion,
                        "latest", latestVersion));
                }, null, 40L); // Delay notification by 2 seconds after join
        }
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}