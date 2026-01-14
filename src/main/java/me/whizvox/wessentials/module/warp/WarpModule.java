package me.whizvox.wessentials.module.warp;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class WarpModule {

    private final Map<String, Location> warps;

    public WarpModule() {
        warps = new Object2ObjectOpenHashMap<>();
    }

    public void load(Configuration config) {
        warps.clear();
        config.getKeys(false).forEach(key -> warps.put(key, config.getLocation(key)));
    }

    public void save(Configuration config) {
        warps.keySet().stream()
            .sorted()
            .forEach(key -> config.set(key, warps.get(key)));
    }

    public Collection<String> getWarpNames() {
        return warps.keySet();
    }

    public @Nullable Location getWarp(String name) {
        return warps.get(name);
    }

    public void set(String name, Location loc) {
        warps.put(name, loc);
    }

    public void delete(String name) {
        warps.remove(name);
    }

}
