package com.example.rtpextended;

import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public final class HomeCommand implements CommandExecutor, TabCompleter {
    private final RTPExtendedPlugin plugin;
    private final HomeManager manager;
    public HomeCommand(RTPExtendedPlugin plugin, HomeManager manager) { this.plugin = plugin; this.manager = manager; }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage(plugin.msg("Only players can use this command.")); return true; }
        switch (command.getName().toLowerCase()) {
            case "sethome" -> {
                if (args.length != 1 || !valid(args[0])) { player.sendMessage(plugin.msg("Usage: /sethome <name>")); return true; }
                if (!manager.setHome(player, args[0])) { player.sendMessage(plugin.msg("You've reached your maximum of " + manager.getMaxHomes(player.getUniqueId()) + " homes.")); return true; }
                player.sendMessage(plugin.msg("Home &b" + args[0] + "&r set."));
            }
            case "home" -> {
                if (args.length != 1) { player.sendMessage(plugin.msg("Usage: /home <name>")); return true; }
                Location loc = manager.getHome(player.getUniqueId(), args[0]);
                if (loc == null) { player.sendMessage(plugin.msg("That home doesn't exist.")); return true; }
                player.teleportAsync(loc).thenAccept(ok -> { if (ok) player.sendMessage(plugin.msg("Teleported home &b" + args[0] + "&r.")); });
            }
            case "homes" -> {
                var names = manager.getHomes(player.getUniqueId()).keySet();
                player.sendMessage(plugin.msg("Homes (&b" + names.size() + "&r/" + manager.getMaxHomes(player.getUniqueId()) + "): " + (names.isEmpty() ? "none" : String.join(", ", names))));
            }
            case "delhome" -> {
                if (args.length != 1) { player.sendMessage(plugin.msg("Usage: /delhome <name>")); return true; }
                if (manager.deleteHome(player.getUniqueId(), args[0])) player.sendMessage(plugin.msg("Deleted home &b" + args[0] + "&r."));
                else player.sendMessage(plugin.msg("That home doesn't exist."));
            }
        }
        return true;
    }

    private boolean valid(String name) { return name.length() <= 32 && name.matches("[A-Za-z0-9_-]+") && !name.equalsIgnoreCase("none"); }
    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player p) || args.length != 1) return List.of();
        return manager.getHomes(p.getUniqueId()).keySet().stream().filter(n -> n.startsWith(args[0].toLowerCase())).toList();
    }
}
