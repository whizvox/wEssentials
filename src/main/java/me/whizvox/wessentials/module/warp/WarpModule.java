package me.whizvox.wessentials.module.warp;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.wessentials.module.SerializableModule;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

@NotNullByDefault
public class WarpModule extends SerializableModule {

    private final Map<String, Location> warps;

    public WarpModule(Plugin plugin) {
        super(plugin, "warps.yml", false);
        warps = new Object2ObjectOpenHashMap<>();
    }

    @Override
    protected void loadFrom(Configuration config) {
        warps.clear();
        config.getKeys(false).forEach(key -> warps.put(key, config.getLocation(key)));
    }

    @Override
    protected void saveTo(Configuration config) {
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
