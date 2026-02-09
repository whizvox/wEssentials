package me.whizvox.wessentials;

import org.bukkit.configuration.Configuration;

public class PluginConfigurationGenerator extends ConfigurationGenerator {

    public PluginConfigurationGenerator(Configuration config) {
        super(config);
    }

    @Override
    public void generateDefaults() {
        set("module.back", true, "Enable player back locations when they die, warp, or teleport.");
        set("module.chat", true, "Enable custom chat formatting, specifically join, leave, and chat messages");
        set("module.customtext", true, "Enable custom text commands.");
        set("module.home", true, "Enable player-set home locations with /home and /sethome.");
        set("module.kit", true, "Enable kits with /kit.");
        set("module.nickname", true, "Enable player nicknames with /nick.");
        set("module.teleportrequest", true, "Enable player teleport requests with /tpa and /tphere.");
        set("module.warp", true, "Enable warp points with /warp.");
        setComments("module", "List of modules for this plugin.", "Changing any of these values requires a server restart to take effect.");
    }

}
