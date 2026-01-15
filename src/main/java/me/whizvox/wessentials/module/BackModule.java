package me.whizvox.wessentials.module;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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

import java.util.*;

public class BackModule extends SerializableModule implements Listener {

    private final Map<UUID, Location> locations;
    private final List<Player> backtracking;

    public final Permission
        deathPermission,
        tpaPermission;
    public final Map<PlayerTeleportEvent.TeleportCause, Permission> causePermissions;

    public BackModule(Plugin plugin) {
        super(plugin, "back.yml", false);
        locations = new Object2ObjectOpenHashMap<>();
        backtracking = new ArrayList<>();
        deathPermission = new Permission("wessentials.back.cause.death");
        tpaPermission = new Permission("wessentials.back.cause.tpa");
        Map<PlayerTeleportEvent.TeleportCause, Permission> causePermissionsTemp = new Object2ObjectOpenHashMap<>();
        for (PlayerTeleportEvent.TeleportCause cause : PlayerTeleportEvent.TeleportCause.values()) {
            String camelCaseName = StringUtil.snakeToCamelCase(cause.toString());
            Permission permission = new Permission("wessentials.back.cause." + camelCaseName);
            causePermissionsTemp.put(cause, permission);
        }
        causePermissions = Collections.unmodifiableMap(causePermissionsTemp);
    }

    @Override
    protected void loadFrom(Configuration config) {
        locations.clear();
        ConfigurationSection locationsSection = config.getObject("locations", ConfigurationSection.class);
        if (locationsSection != null) {
            locationsSection.getValues(false)
                .forEach((playerId, location) -> locations.put(UUID.fromString(playerId), (Location) location));
        }
    }

    @Override
    protected void saveTo(Configuration config) {
        Map<String, Location> locationsMap = new Object2ObjectArrayMap<>(locations.size());
        locations.forEach((player, location) -> locationsMap.put(player.toString(), location));
        config.set("locations", locationsMap);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!isBacktracking(player)) {
            Permission causePermission = causePermissions.get(event.getCause());
            if (causePermission != null && player.hasPermission(causePermission)) {
                setLocation(player, event.getFrom());
                save();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(deathPermission)) {
            setLocation(player, player.getLocation());
            save();
        }
    }

    public void setLocation(Player player, Location prevLocation) {
        locations.put(player.getUniqueId(), prevLocation);
    }

    public @Nullable Location getLocation(Player player) {
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
