package com.msncakma.msnairdrop;

import com.msncakma.msnairdrop.config.LanguageManager;
import com.msncakma.msnairdrop.utils.AreaManager;
import com.msncakma.msnairdrop.events.EventManager;
import com.msncakma.msnairdrop.drops.DropManager;
import com.msncakma.msnairdrop.utils.UpdateChecker;
import com.msncakma.msnairdrop.utils.DebugManager;
import com.msncakma.msnairdrop.rewards.RewardManager;
import com.msncakma.msnairdrop.utils.MessageManager;
import com.msncakma.msnairdrop.commands.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import com.msncakma.msnairdrop.scheduler.TaskScheduler;
import com.msncakma.msnairdrop.commands.*;
import org.bukkit.plugin.java.JavaPlugin;

public class MsnAirDrop extends JavaPlugin {
    private static MsnAirDrop instance;
    private LanguageManager languageManager;
    private AreaManager areaManager;
    private EventManager eventManager;
    private DropManager dropManager;
    private UpdateChecker updateChecker;
    private DebugManager debugManager;
    private RewardManager rewardManager;
    private MessageManager messageManager;
    private TaskScheduler taskScheduler;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize task scheduler
        this.taskScheduler = new TaskScheduler(this);
        
        // Load configurations
        saveDefaultConfig();
        
        // Initialize managers
        this.languageManager = new LanguageManager(this);
        this.areaManager = new AreaManager();
        this.eventManager = new EventManager(this);
        this.dropManager = new DropManager(this);
        this.debugManager = new DebugManager(this);
        this.rewardManager = new RewardManager(this);
        this.messageManager = new MessageManager(this);
        
        // Register commands
        registerCommands();
        
        // Initialize update checker
        if (getConfig().getBoolean("settings.check-updates", true)) {
            this.updateChecker = new UpdateChecker(this);
        }
        
        getLogger().info("MsnAirDrop has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MsnAirDrop has been disabled!");
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    private void registerCommands() {
        // Create the main command handler that will handle all msnairdrop subcommands
        MainCommandExecutor mainExecutor = new MainCommandExecutor();
        
        // Register all subcommand handlers
        mainExecutor.registerSubcommand("version", new VersionCommand(this));
        mainExecutor.registerSubcommand("pos1", new PositionCommand(this, areaManager));
        mainExecutor.registerSubcommand("pos2", new PositionCommand(this, areaManager));
        mainExecutor.registerSubcommand("event", new EventCommand(this, eventManager, areaManager));
        mainExecutor.registerSubcommand("reload", new ReloadCommand(this));
        mainExecutor.registerSubcommand("debug", new DebugCommand(this));
        mainExecutor.registerSubcommand("reward", new RewardCommand(this));
        
        // Set the main command executor
        PluginCommand mainCommand = getCommand("msnairdrop");
        if (mainCommand != null) {
            mainCommand.setExecutor(mainExecutor);
        }
        
        // Register the stats command separately since it's not a subcommand
        PluginCommand statsCommand = getCommand("airdropstats");
        if (statsCommand != null) {
            statsCommand.setExecutor(new StatsCommand(this));
        }
    }

    public void checkForUpdates() {
        if (updateChecker != null) {
            updateChecker = new UpdateChecker(this);
        }
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public DebugManager getDebugManager() {
        return debugManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public AreaManager getAreaManager() {
        return areaManager;
    }

    public DropManager getDropManager() {
        return dropManager;
    }

    public static MsnAirDrop getInstance() {
        return instance;
    }

    public TaskScheduler getTaskScheduler() {
        return taskScheduler;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }
}