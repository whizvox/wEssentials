package me.whizvox.wessentials.module.back;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record BackLocation(Location location,
                           boolean isSafe,
                           @Nullable Location safeLocation) implements ConfigurationSerializable {

    public BackLocation(Map<String, Object> obj) {
        this(
            (Location) obj.get("location"),
            (Boolean) obj.get("isSafe"),
            obj.containsKey("safeLocation") ? (Location) obj.get("safeLocation") : null
        );
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        if (safeLocation == null) {
            return Map.of("location", location, "isSafe", isSafe);
        }
        return Map.of("location", location, "isSafe", isSafe, "safeLocation", safeLocation);
    }

}
