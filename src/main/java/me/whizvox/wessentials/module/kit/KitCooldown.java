package me.whizvox.wessentials.module.kit;

import me.whizvox.wessentials.util.StringUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNullByDefault;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@NotNullByDefault
public record KitCooldown(UUID player, String kit, LocalDateTime expires) implements ConfigurationSerializable {

    public KitCooldown(Map<String, Object> obj) {
        this(
            UUID.fromString((String) obj.get("player")),
            (String) obj.get("kit"),
            StringUtil.parseDateTime((String) obj.get("expires"))
        );
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of(
            "player", player.toString(),
            "kit", kit,
            "expires", StringUtil.formatDateTime(expires)
        );
    }

}
