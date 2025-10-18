package com.msncakma.msnairdrop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainCommandExecutor implements CommandExecutor, TabCompleter {
    private final Map<String, CommandExecutor> subcommands = new HashMap<>();
    
    public void registerSubcommand(String name, CommandExecutor executor) {
        subcommands.put(name.toLowerCase(), executor);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Show help message
            command.setUsage(command.getUsage());
            return false;
        }
        
        String subcommand = args[0].toLowerCase();
        CommandExecutor executor = subcommands.get(subcommand);
        
        if (executor == null) {
            // Subcommand not found, show usage
            command.setUsage(command.getUsage());
            return false;
        }
        
        // Remove the subcommand from args and pass the rest to the executor
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        
        return executor.onCommand(sender, command, label, newArgs);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            // Return available subcommands that the sender has permission for
            List<String> completions = new ArrayList<>();
            String partialCommand = args.length > 0 ? args[0].toLowerCase() : "";
            
            for (String subcommand : subcommands.keySet()) {
                if (subcommand.startsWith(partialCommand) && 
                    (sender.hasPermission("msnairdrop." + subcommand) || 
                     sender.hasPermission("msnairdrop.admin"))) {
                    completions.add(subcommand);
                }
            }
            
            return completions;
        }
        
        // If a subcommand executor implements TabCompleter, use its tab completion
        String subcommand = args[0].toLowerCase();
        CommandExecutor executor = subcommands.get(subcommand);
        
        if (executor instanceof TabCompleter) {
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
            
            return ((TabCompleter) executor).onTabComplete(sender, command, label, newArgs);
        }
        
        return new ArrayList<>();
    }
}