package me.whizvox.wessentials.module.nick;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public record Nickname(UUID player, String nickname, boolean plain) implements ConfigurationSerializable {

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of("player", player.toString(), "nickname", nickname, "plain", plain);
    }

    public static Nickname deserialize(Map<String, Object> args) {
        return new Nickname(UUID.fromString((String) args.get("player")), (String) args.get("nickname"), (Boolean) args.get("plain"));
    }

}
