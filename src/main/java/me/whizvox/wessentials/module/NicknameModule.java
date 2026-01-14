package me.whizvox.wessentials.module;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Map;
import java.util.UUID;

public class NicknameModule {

    private final MiniMessage miniMessage;
    private final Permission
        colorPermission;

    private final Map<UUID, String> nicknames;

    public NicknameModule() {
        miniMessage = MiniMessage.builder()
            .tags(TagResolver.builder()
                .resolvers(StandardTags.color(), StandardTags.decorations(), StandardTags.gradient(),
                    StandardTags.rainbow(), StandardTags.reset(), StandardTags.pride(), StandardTags.shadowColor())
                .build())
            .build();
        colorPermission = new Permission("wessentials.nick.color");
        nicknames = new Object2ObjectOpenHashMap<>();
    }

    public void load(Configuration config) {
        nicknames.clear();
        config.getKeys(false).forEach(key -> nicknames.put(UUID.fromString(key), config.getString(key)));
    }

    public void save(Configuration config) {
        nicknames.forEach((key, value) -> config.set(key.toString(), value));
    }

    public Component setNickname(CommandSender sender, Player receiver, String nickname) {
        Component nameComp;
        if (sender.hasPermission(colorPermission)) {
            nameComp = miniMessage.deserialize(nickname);
        } else {
            nameComp = Component.text(nickname);
        }
        receiver.displayName(nameComp);
        nicknames.put(receiver.getUniqueId(), nickname);
        return nameComp;
    }

    public boolean clearNickname(Player player) {
        if (nicknames.remove(player.getUniqueId()) != null) {
            player.displayName(Component.text(player.getName()));
            return true;
        }
        return false;
    }

}
