package com.msncakma.msnairdrop.scheduler;

import com.msncakma.msnairdrop.MsnAirDrop;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TaskScheduler {
    private final MsnAirDrop plugin;
    private final boolean isFolia;

    public TaskScheduler(MsnAirDrop plugin) {
        this.plugin = plugin;
        this.isFolia = checkFolia();
    }

    private boolean checkFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void runTask(Runnable task) {
        if (isFolia) {
            plugin.getServer().getGlobalRegionScheduler().execute(plugin, task);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public void runTaskAsync(Runnable task) {
        if (isFolia) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, (scheduledTask) -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    public void runTaskLater(Runnable task, long delay) {
        if (isFolia) {
            plugin.getServer().getGlobalRegionScheduler().executeDelayed(plugin, task, delay);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delay);
        }
    }

    public void runRegionTask(Location location, Runnable task) {
        if (isFolia) {
            plugin.getServer().getRegionScheduler().execute(plugin, location, task);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public CompletableFuture<Void> runRegionTaskAsync(Location location, Runnable task) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (isFolia) {
            plugin.getServer().getRegionScheduler().executeAsync(plugin, location, () -> {
                try {
                    task.run();
                    future.complete(null);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    task.run();
                    future.complete(null);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
        }
        return future;
    }

    public boolean isFolia() {
        return isFolia;
    }
}