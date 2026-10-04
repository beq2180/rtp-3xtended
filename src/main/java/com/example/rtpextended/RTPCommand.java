package com.example.rtpextended;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public final class RTPCommand implements CommandExecutor, TabCompleter {
    private final RTPExtendedPlugin plugin;
    private final RTPManager manager;
    public RTPCommand(RTPExtendedPlugin plugin, RTPManager manager) { this.plugin = plugin; this.manager = manager; }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage(plugin.msg("Only players can use /rtp.")); return true; }
        if (args.length > 1) { player.sendMessage(plugin.msg("Usage: /rtp [o|n|e|overworld|nether|end]")); return true; }
        String target = args.length == 0 ? "overworld" : args[0];
        World world = manager.resolveWorld(player, target);
        if (world == null) { player.sendMessage(plugin.msg("Unknown or unavailable dimension. Use overworld, nether, or end.")); return true; }
        player.sendMessage(plugin.msg("Finding a safe location in " + pretty(world.getEnvironment()) + "..."));
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Location location = manager.findSafeLocation(world);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (location == null) { player.sendMessage(plugin.msg("Couldn't find a safe location. Try again.")); return; }
                if (!player.isOnline()) return;
                player.teleportAsync(location).thenAccept(ok -> {
                    if (ok) player.sendMessage(plugin.msg("Teleported to a random location in " + pretty(world.getEnvironment()) + "."));
                    else player.sendMessage(plugin.msg("Teleport failed. Try again."));
                });
            });
        });
        return true;
    }

    private String pretty(World.Environment env) { return switch (env) { case NORMAL -> "the Overworld"; case NETHER -> "the Nether"; case THE_END -> "the End"; }; }
    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) return List.of();
        return List.of("o", "n", "e", "overworld", "nether", "end").stream().filter(s -> s.startsWith(args[0].toLowerCase())).toList();
    }
}
