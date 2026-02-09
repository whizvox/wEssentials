package me.whizvox.wessentials.module.chat;

import me.whizvox.wessentials.ConfigurationGenerator;
import org.bukkit.configuration.Configuration;

public class ChatConfigurationGenerator extends ConfigurationGenerator {

    public ChatConfigurationGenerator(Configuration config) {
        super(config);
    }

    @Override
    public void generateDefaults() {
        set("format.player", "<prefix><player_name><suffix>",
            "Set how the player name will be displayed in the following messages",
            "Available variables:",
            "- <player_name>: The player's username, which may be changed by plugins",
            "- <prefix>: The player's prefix, usually provided by a plugin like LuckPerms",
            "- <suffix>: The player's suffix, usually provided by a plugin like LuckPerms");
        set("format.chat", "<player> <reset><bold>:</bold> <gray><message></gray>",
            "Format of a player's chat message.",
            "Available variables: <player>, <message>");
        set("format.join", "<player> <green>has joined the server</green>",
            "Format of the chat message shown when a player joins the server.",
            "Available variables: <player>");
        set("format.leave", "<player> <yellow>has left the server</yellow>",
            "Format of the chat message shown when a player joins the server.",
            "Available variables: <player>");
        setComments("format",
            "Changes chat message formatting for all players. All formats support MiniMessage syntax:",
            "https://webui.advntr.dev/",
            "The following variables may be used in most contexts unless stated otherwise:",
            "- <player>: Formatted player name (see `format.player` for this variable)",
            "- <message>: Chat message sent by a player");
    }

}
