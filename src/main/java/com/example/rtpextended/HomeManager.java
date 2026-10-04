package com.example.rtpextended;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class HomeManager {
    private final RTPExtendedPlugin plugin;
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();
    private final Map<UUID, Integer> maxHomes = new HashMap<>();
    private File file;
    private YamlConfiguration data;

    public HomeManager(RTPExtendedPlugin plugin) { this.plugin = plugin; }

    public void load() {
        file = new File(plugin.getDataFolder(), "homes.yml");
        data = YamlConfiguration.loadConfiguration(file);
        homes.clear();
        maxHomes.clear();
        ConfigurationSection players = data.getConfigurationSection("players");
        if (players == null) return;
        for (String uuidText : players.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidText);
                ConfigurationSection section = players.getConfigurationSection(uuidText);
                if (section == null) continue;
                if (section.contains("max-homes")) maxHomes.put(uuid, section.getInt("max-homes"));
                ConfigurationSection hs = section.getConfigurationSection("homes");
                if (hs == null) continue;
                Map<String, Location> map = homes.computeIfAbsent(uuid, k -> new LinkedHashMap<>());
                for (String name : hs.getKeys(false)) {
                    Location loc = hs.getLocation(name);
                    if (loc != null) map.put(name.toLowerCase(Locale.ROOT), loc);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save() {
        if (data == null) data = new YamlConfiguration();
        data.set("players", null);
        for (UUID uuid : homes.keySet()) {
            String base = "players." + uuid;
            for (Map.Entry<String, Location> e : homes.getOrDefault(uuid, Map.of()).entrySet()) {
                data.set(base + ".homes." + e.getKey(), e.getValue());
            }
            if (maxHomes.containsKey(uuid)) data.set(base + ".max-homes", maxHomes.get(uuid));
        }
        for (UUID uuid : maxHomes.keySet()) {
            if (!homes.containsKey(uuid)) data.set("players." + uuid + ".max-homes", maxHomes.get(uuid));
        }
        try { data.save(file); } catch (IOException e) { plugin.getLogger().severe("Could not save homes.yml: " + e.getMessage()); }
    }

    public int getDefaultMaxHomes() { return Math.max(0, plugin.getConfig().getInt("default-max-homes", 3)); }
    public int getMaxHomes(UUID uuid) { return maxHomes.getOrDefault(uuid, getDefaultMaxHomes()); }
    public void setMaxHomes(UUID uuid, int amount) { maxHomes.put(uuid, Math.max(0, amount)); save(); }
    public Map<String, Location> getHomes(UUID uuid) { return Collections.unmodifiableMap(homes.getOrDefault(uuid, Map.of())); }
    public Location getHome(UUID uuid, String name) { return homes.getOrDefault(uuid, Map.of()).get(name.toLowerCase(Locale.ROOT)); }
    public boolean hasHome(UUID uuid, String name) { return getHome(uuid, name) != null; }

    public boolean setHome(Player player, String name) {
        String key = name.toLowerCase(Locale.ROOT);
        Map<String, Location> map = homes.computeIfAbsent(player.getUniqueId(), k -> new LinkedHashMap<>());
        if (!map.containsKey(key) && map.size() >= getMaxHomes(player.getUniqueId())) return false;
        map.put(key, player.getLocation().clone());
        save();
        return true;
    }

    public boolean deleteHome(UUID uuid, String name) {
        Map<String, Location> map = homes.get(uuid);
        if (map == null) return false;
        boolean removed = map.remove(name.toLowerCase(Locale.ROOT)) != null;
        if (removed) save();
        return removed;
    }
}
