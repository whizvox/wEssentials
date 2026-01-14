package me.whizvox.wessentials.module.chat;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@NotNullByDefault
public class ChatModule implements Listener, ChatRenderer {

    private @Nullable String playerFormat;
    private @Nullable String chatFormat;
    private @Nullable String joinFormat;
    private @Nullable String leaveFormat;

    private final PrefixSuffixProvider prefixSuffixProvider;

    public ChatModule(PrefixSuffixProvider prefixSuffixProvider) {
        playerFormat = null;
        chatFormat = null;
        joinFormat = null;
        leaveFormat = null;
        this.prefixSuffixProvider = prefixSuffixProvider;
    }

    public void load(Configuration config) {
        playerFormat = config.getString("format.player");
        chatFormat = config.getString("format.chat");
        joinFormat = config.getString("format.join");
        leaveFormat = config.getString("format.leave");
    }

    @SuppressWarnings("PatternValidation")
    private Component format(String format, Player player, Map<String, Object> args) {
        Component playerName;
        if (playerFormat == null) {
            playerName = player.displayName();
        } else {
            playerName = MiniMessage.miniMessage().deserialize(
                playerFormat
                    .replace("<prefix>", prefixSuffixProvider.getPrefix(player))
                    .replace("<suffix>", prefixSuffixProvider.getSuffix(player)),
                Placeholder.component("player", player.displayName()),
                Placeholder.unparsed("player_name", player.getName()));
        }
        TagResolver[] placeholders = new TagResolver[args.size() + 1];
        placeholders[0] = Placeholder.component("player", playerName);
        int i = 1;
        for (String key : args.keySet()) {
            Object arg = args.get(key);
            if (arg instanceof ComponentLike compLike) {
                placeholders[i] = Placeholder.component(key, compLike);
            } else {
                placeholders[i] = Placeholder.component(key, Component.text(String.valueOf(arg)));
            }
            i++;
        }
        return MiniMessage.miniMessage().deserialize(format, placeholders);
    }

    public Component format(Player player, Component message) {
        assert chatFormat != null;
        return format(chatFormat, player, Map.of("message", message));
    }

    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience viewer) {
        return format(source, message);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (chatFormat != null) {
            event.renderer(this);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (joinFormat != null) {
            event.joinMessage(format(joinFormat, event.getPlayer(), Map.of()));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (leaveFormat != null) {
            event.quitMessage(format(leaveFormat, event.getPlayer(), Map.of()));
        }
    }

}
