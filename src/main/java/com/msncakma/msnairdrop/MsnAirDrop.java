package com.msncakma.msnairdrop;

import com.msncakma.msnairdrop.config.LanguageManager;
import com.msncakma.msnairdrop.utils.AreaManager;
import com.msncakma.msnairdrop.events.EventManager;
import com.msncakma.msnairdrop.drops.DropManager;
import com.msncakma.msnairdrop.utils.UpdateChecker;
import com.msncakma.msnairdrop.utils.DebugManager;
import com.msncakma.msnairdrop.rewards.RewardManager;
import com.msncakma.msnairdrop.utils.MessageManager;
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
        if (messageManager != null) {
            messageManager.close();
        }
        getLogger().info("MsnAirDrop has been disabled!");
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    private void registerCommands() {
        getCommand("msnairdrop").setExecutor(new VersionCommand(this));
        
        PositionCommand positionCommand = new PositionCommand(this, areaManager);
        getCommand("msnairdrop:pos1").setExecutor(positionCommand);
        getCommand("msnairdrop:pos2").setExecutor(positionCommand);
        
        getCommand("msnairdrop event").setExecutor(new EventCommand(this, eventManager, areaManager));
        getCommand("msnairdrop reload").setExecutor(new ReloadCommand(this));
        getCommand("msnairdrop debug").setExecutor(new DebugCommand(this));
        getCommand("msnairdrop reward").setExecutor(new RewardCommand(this));
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