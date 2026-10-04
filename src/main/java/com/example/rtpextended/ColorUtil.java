package com.example.rtpextended;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtil {
    private static final Pattern HEX = Pattern.compile("#([A-Fa-f0-9]{6})");
    private ColorUtil() {}

    public static String color(String input) {
        Matcher matcher = HEX.matcher(input);
        StringBuffer out = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(out, Matcher.quoteReplacement(ChatColor.of("#" + matcher.group(1)).toString()));
        }
        matcher.appendTail(out);
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', out.toString());
    }
}
