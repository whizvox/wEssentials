package me.whizvox.wessentials.module.nick;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NicknameModule {

    private final MiniMessage miniMessage;
    private final Permission colorPermission;
    private final Map<UUID, Nickname> nicknames;

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
        //noinspection unchecked
        List<Nickname> nicknamesList = (List<Nickname>) config.getList("nicknames");
        if (nicknamesList != null) {
            nicknamesList.forEach(nickname -> nicknames.put(nickname.player(), nickname));
        }
    }

    public void save(Configuration config) {
        List<Nickname> nicknamesList = new ArrayList<>(nicknames.values());
        config.set("nicknames", nicknamesList);
    }

    @Nullable
    public Component getNickname(Player player) {
        Nickname nickname = nicknames.get(player.getUniqueId());
        if (nickname == null) {
            return null;
        }
        if (nickname.plain()) {
            return Component.text(nickname.nickname());
        }
        return miniMessage.deserialize(nickname.nickname());
    }

    public Component setNickname(CommandSender sender, Player receiver, String nickname) {
        Component nameComp;
        boolean plain;
        if (sender.hasPermission(colorPermission)) {
            nameComp = miniMessage.deserialize(nickname);
            plain = false;
        } else {
            nameComp = Component.text(nickname);
            plain = true;
        }
        receiver.displayName(nameComp);
        nicknames.put(receiver.getUniqueId(), new Nickname(receiver.getUniqueId(), nickname, plain));
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
