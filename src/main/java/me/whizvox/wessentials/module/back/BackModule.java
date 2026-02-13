package me.whizvox.wessentials.module.back;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.wessentials.ConfigurationGenerator;
import me.whizvox.wessentials.module.SerializableModule;
import me.whizvox.wessentials.util.LocationUtil;
import me.whizvox.wessentials.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.*;

public class BackModule extends SerializableModule implements Listener {

    private final Map<UUID, BackLocation> locations;
    private final List<Player> backtracking;

    public final Permission
        deathPermission,
        homePermission,
        tpaPermission,
        unsafePermission;
    public final Map<PlayerTeleportEvent.TeleportCause, Permission> causePermissions;

    private boolean safeSearchEnabled;
    private int safeCheckRadius;
    private int safeCheckHeight;
    private final List<Vector3i> safeSearchOffsets;

    public BackModule(Plugin plugin) {
        super("back", plugin, "back.yml", false);
        locations = new Object2ObjectOpenHashMap<>();
        backtracking = new ArrayList<>();
        deathPermission = new Permission("wessentials.back.update.death");
        homePermission = new Permission("wessentials.back.update.home");
        tpaPermission = new Permission("wessentials.back.update.tpa");
        unsafePermission = new Permission("wessentials.back.unsafe");
        Map<PlayerTeleportEvent.TeleportCause, Permission> causePermissionsTemp = new Object2ObjectOpenHashMap<>();
        for (PlayerTeleportEvent.TeleportCause cause : PlayerTeleportEvent.TeleportCause.values()) {
            String camelCaseName = StringUtil.snakeToCamelCase(cause.toString());
            Permission permission = new Permission("wessentials.back.update." + camelCaseName);
            causePermissionsTemp.put(cause, permission);
        }
        causePermissions = Collections.unmodifiableMap(causePermissionsTemp);
        safeSearchEnabled = true;
        safeCheckRadius = 5;
        safeCheckHeight = 80;
        safeSearchOffsets = new ArrayList<>();
    }

    public boolean isSafeSearchEnabled() {
        return safeSearchEnabled;
    }

    @Override
    public @Nullable ConfigurationGenerator getConfigurationGenerator(Configuration config) {
        return new BackConfigurationGenerator(config);
    }

    @Override
    protected void loadFrom(Configuration config) {
        safeSearchEnabled = config.getBoolean("config.enableSafeSearch");
        safeCheckRadius = config.getInt("config.safeCheckRadius");
        safeCheckHeight = config.getInt("config.safeCheckHeight");
        safeSearchOffsets.clear();
        if (safeSearchEnabled) {
            for (int x = -safeCheckRadius; x <= safeCheckRadius; x++) {
                for (int z = -safeCheckRadius; z <= safeCheckRadius; z++) {
                    for (int y = -safeCheckRadius; y <= safeCheckHeight; y++) {
                        safeSearchOffsets.add(new Vector3i(x, y, z));
                    }
                }
            }
            final Vector3i origin = new Vector3i(0, 0, 0);
            safeSearchOffsets.sort(Comparator.comparingDouble(offset -> offset.distanceSquared(origin)));
        }
        locations.clear();
        ConfigurationSection locationsSection = config.getObject("locations", ConfigurationSection.class);
        if (locationsSection != null) {
            locationsSection.getValues(false)
                .forEach((playerId, location) -> locations.put(UUID.fromString(playerId), (BackLocation) location));
        }
    }

    @Override
    protected void saveTo(Configuration config) {
        Map<String, BackLocation> locationsMap = new Object2ObjectArrayMap<>(locations.size());
        locations.forEach((player, location) -> locationsMap.put(player.toString(), location));
        config.set("locations", locationsMap);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!isBacktracking(player)) {
            Permission causePermission = causePermissions.get(event.getCause());
            if (causePermission != null && player.hasPermission(causePermission)) {
                BackLocation loc = determineLocation(event.getFrom());
                setLocation(player, loc);
                save();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(deathPermission)) {
            BackLocation loc = determineLocation(player.getLocation());
            setLocation(player, loc);
            save();
        }
    }

    public @Nullable Location findSafeLocation(Location location) {
        int yOff;
        if (location.getBlockY() < location.getWorld().getMinHeight()) {
            yOff = location.getWorld().getMinHeight() - location.getBlockY();
        } else {
            yOff = 0;
        }
        for (Vector3i offset : safeSearchOffsets) {
            Location loc = location.clone().add(offset.x, offset.y + yOff, offset.z);
            if (LocationUtil.isSafe(loc)) {
                return loc;
            }
        }
        return null;
    }

    public BackLocation determineLocation(Location prevLocation) {
        Location safe;
        boolean isSafe = LocationUtil.isSafe(prevLocation);
        if (isSafe || !safeSearchEnabled) {
            safe = null;
        } else {
            safe = findSafeLocation(prevLocation);
        }
        return new BackLocation(prevLocation, isSafe, safe);
    }

    public void setLocation(Player player, BackLocation location) {
        locations.put(player.getUniqueId(), location);
    }

    public void setLocation(Player player, Location prevLocation) {
        setLocation(player, determineLocation(prevLocation));
    }

    public @Nullable BackLocation getLocation(Player player) {
        return locations.get(player.getUniqueId());
    }

    public void clearLocation(Player player) {
        locations.remove(player.getUniqueId());
    }

    public boolean isBacktracking(Player player) {
        return backtracking.contains(player);
    }

    public void markBacktracking(Player player) {
        backtracking.add(player);
    }

    public void unmarkBacktracking(Player player) {
        backtracking.remove(player);
    }

}
