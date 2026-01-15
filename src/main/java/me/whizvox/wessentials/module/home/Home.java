package me.whizvox.wessentials.module.home;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Map;
import java.util.UUID;

@NotNullByDefault
public record Home(UUID player, Location location, boolean bed) implements ConfigurationSerializable {

    @Override
    public Map<String, Object> serialize() {
        return Map.of("player", player.toString(), "location", location, "bed", bed);
    }

    public static Home deserialize(Map<String, Object> obj) {
        return new Home(UUID.fromString((String) obj.get("player")), (Location) obj.get("location"), (Boolean) obj.get("bed"));
    }

}
