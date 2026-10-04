package com.example.rtpextended;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public final class RTPManager {
    private final RTPExtendedPlugin plugin;
    public RTPManager(RTPExtendedPlugin plugin) { this.plugin = plugin; }

    public World resolveWorld(Player player, String input) {
        String arg = input == null ? "overworld" : input.toLowerCase();
        return switch (arg) {
            case "o", "overworld" -> Bukkit.getWorlds().stream().filter(w -> w.getEnvironment() == World.Environment.NORMAL).findFirst().orElse(null);
            case "n", "nether" -> Bukkit.getWorlds().stream().filter(w -> w.getEnvironment() == World.Environment.NETHER).findFirst().orElse(null);
            case "e", "end" -> Bukkit.getWorlds().stream().filter(w -> w.getEnvironment() == World.Environment.THE_END).findFirst().orElse(null);
            default -> null;
        };
    }

    public Location findSafeLocation(World world) {
        String key = switch (world.getEnvironment()) {
            case NORMAL -> "overworld";
            case NETHER -> "nether";
            case THE_END -> "end";
        };
        int min = Math.max(0, plugin.getConfig().getInt("rtp." + key + ".min-radius", 500));
        int max = Math.max(min + 1, plugin.getConfig().getInt("rtp." + key + ".max-radius", 5000));
        int attempts = Math.max(1, plugin.getConfig().getInt("rtp.max-attempts", 20));

        for (int i = 0; i < attempts; i++) {
            double angle = ThreadLocalRandom.current().nextDouble(0, Math.PI * 2);
            double radius = ThreadLocalRandom.current().nextDouble(min, max);
            int x = (int) Math.round(Math.cos(angle) * radius);
            int z = (int) Math.round(Math.sin(angle) * radius);
            int y;
            try {
                y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
            } catch (Exception ignored) { continue; }
            Location ground = new Location(world, x + 0.5, y, z + 0.5);
            if (isSafe(ground)) return ground.add(0, 1, 0);
        }
        return null;
    }

    private boolean isSafe(Location loc) {
        Material feet = loc.getBlock().getType();
        Material ground = loc.clone().subtract(0, 1, 0).getBlock().getType();
        Material head = loc.clone().add(0, 1, 0).getBlock().getType();
        if (!feet.isAir() || !head.isAir()) return false;
        if (plugin.getConfig().getBoolean("rtp.avoid-water", true) && (ground == Material.WATER || ground == Material.BUBBLE_COLUMN)) return false;
        if (plugin.getConfig().getBoolean("rtp.avoid-lava", true) && (ground == Material.LAVA || feet == Material.LAVA || head == Material.LAVA)) return false;
        if (plugin.getConfig().getBoolean("rtp.avoid-fire", true) && (ground == Material.FIRE || ground == Material.SOUL_FIRE)) return false;
        if (plugin.getConfig().getBoolean("rtp.avoid-leaves", true) && ground.name().endsWith("_LEAVES")) return false;
        return ground.isSolid() && ground.isBlock();
    }
}
