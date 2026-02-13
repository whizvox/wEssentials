package me.whizvox.wessentials.util;

import org.bukkit.Location;

public class LocationUtil {

    public static boolean isSafe(Location location) {
        Location below = location.clone().add(0, -1, 0);
        Location above = location.clone().add(0, 1, 0);
        return below.getBlock().isCollidable() && location.getBlock().getType().isAir() &&
            above.getBlock().getType().isAir() && location.getWorld().getWorldBorder().isInside(location);
    }

}
