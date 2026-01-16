package me.whizvox.wessentials.module.warp;

import org.bukkit.Location;

public record WarpLocation(String name, Location location) {

    public String getPermission() {
        return getPermission(name);
    }

    public static String getPermission(String name) {
        return "wessentials.warp.access." + name;
    }

}
