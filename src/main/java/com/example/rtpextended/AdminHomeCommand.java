package com.example.rtpextended;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;

import java.util.Arrays;

public final class AdminHomeCommand implements CommandExecutor {
    private final RTPExtendedPlugin plugin;
    private final HomeManager manager;
    public AdminHomeCommand(RTPExtendedPlugin plugin, HomeManager manager) { this.plugin = plugin; this.manager = manager; }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("rtpextended.admin")) { sender.sendMessage(plugin.msg("You don't have permission.")); return true; }
        if (args.length != 2) { sender.sendMessage(plugin.msg("Usage: /setmaxhomes <player> <amount>")); return true; }
        int amount;
        try { amount = Integer.parseInt(args[1]); } catch (NumberFormatException e) { sender.sendMessage(plugin.msg("Amount must be a whole number.")); return true; }
        if (amount < 0 || amount > 100) { sender.sendMessage(plugin.msg("Amount must be between 0 and 100.")); return true; }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) { sender.sendMessage(plugin.msg("That player hasn't joined this server.")); return true; }
        manager.setMaxHomes(target.getUniqueId(), amount);
        sender.sendMessage(plugin.msg("Set &b" + target.getName() + "&r's maximum homes to &b" + amount + "&r."));
        return true;
    }
}
