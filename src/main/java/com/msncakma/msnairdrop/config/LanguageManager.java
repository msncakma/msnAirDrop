package com.msncakma.msnairdrop.config;

import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class LanguageManager {
    private final MsnAirDrop plugin;
    private FileConfiguration langConfig;
    private File langFile;
    private String language;

    public LanguageManager(MsnAirDrop plugin) {
        this.plugin = plugin;
        loadLanguage();
    }

    public void loadLanguage() {
        language = plugin.getConfig().getString("messages.language", "tr-TR");
        
        if (langFile == null) {
            langFile = new File(plugin.getDataFolder(), "messages/" + language + ".yml");
        }

        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            plugin.saveResource("messages/" + language + ".yml", false);
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);

        // Set default values from internal file if they don't exist
        Reader defaultConfigStream = new InputStreamReader(plugin.getResource("messages/" + language + ".yml"), StandardCharsets.UTF_8);
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
        langConfig.setDefaults(defaultConfig);
        langConfig.options().copyDefaults(true);
        
        try {
            langConfig.save(langFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save language file: " + e.getMessage());
        }
    }

    public String getMessage(String path) {
        String message = langConfig.getString(path);
        if (message == null) {
            return "<red>Missing message: " + path + "</red>";
        }
        return message;
    }

    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);
        
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Replacements must be in pairs!");
        }
        
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        
        return plugin.getMessageManager().parseMessage(message);
    }

    public void reload() {
        loadLanguage();
    }
}