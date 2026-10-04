package com.example.rtpextended;

import org.bukkit.plugin.java.JavaPlugin;

public final class RTPExtendedPlugin extends JavaPlugin {
    private HomeManager homeManager;
    private RTPManager rtpManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.homeManager = new HomeManager(this);
        this.homeManager.load();
        this.rtpManager = new RTPManager(this);

        RTPCommand rtp = new RTPCommand(this, rtpManager);
        HomeCommand home = new HomeCommand(this, homeManager);
        AdminHomeCommand admin = new AdminHomeCommand(this, homeManager);

        getCommand("rtp").setExecutor(rtp);
        getCommand("rtp").setTabCompleter(rtp);
        getCommand("sethome").setExecutor(home);
        getCommand("home").setExecutor(home);
        getCommand("home").setTabCompleter(home);
        getCommand("homes").setExecutor(home);
        getCommand("delhome").setExecutor(home);
        getCommand("delhome").setTabCompleter(home);
        getCommand("setmaxhomes").setExecutor(admin);

        getLogger().info("RTPExtended enabled. Default max homes: " + homeManager.getDefaultMaxHomes());
    }

    @Override
    public void onDisable() {
        if (homeManager != null) homeManager.save();
    }

    public String msg(String message) {
        String prefix = getConfig().getString("messages.prefix", "&8[&bRTP&8] &r");
        return ColorUtil.color(prefix + message);
    }
}
