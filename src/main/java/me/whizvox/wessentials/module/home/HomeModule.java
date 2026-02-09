package me.whizvox.wessentials.module.home;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.wessentials.module.SerializableModule;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NotNullByDefault
public class HomeModule extends SerializableModule {

    private final Map<UUID, Home> homes;

    public HomeModule(Plugin plugin) {
        super("home", plugin, "homes.yml", false);
        homes = new Object2ObjectOpenHashMap<>();
    }

    @Override
    protected void loadFrom(Configuration config) {
        homes.clear();
        List<Home> homesList = (List<Home>) config.getList("homes");
        if (homesList != null) {
            homesList.forEach(home -> homes.put(home.player(), home));
        }
    }

    @Override
    protected void saveTo(Configuration config) {
        List<Home> homesList = new ArrayList<>(homes.values());
        config.set("homes", homesList);
    }

    public void setHome(Player player, Location location, boolean isBed) {
        homes.put(player.getUniqueId(), new Home(player.getUniqueId(), location, isBed));
    }

    public boolean setHome(Player player, boolean isBed) {
        Location location;
        if (isBed) {
            location = player.getRespawnLocation();
            if (location == null) {
                return false;
            }
        } else {
            location = player.getLocation();
        }
        setHome(player, location, isBed);
        return true;
    }

    @Nullable
    public Location getHome(Player player) {
        Home home = homes.get(player.getUniqueId());
        if (home == null) {
            return null;
        }
        return home.location();
    }

    public void clearHome(Player player) {
        homes.remove(player.getUniqueId());
    }

}
