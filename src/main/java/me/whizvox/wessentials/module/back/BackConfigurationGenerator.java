package me.whizvox.wessentials.module.back;

import me.whizvox.wessentials.ConfigurationGenerator;
import org.bukkit.configuration.Configuration;

import java.util.Map;

public class BackConfigurationGenerator extends ConfigurationGenerator {

    public BackConfigurationGenerator(Configuration config) {
        super(config);
    }

    @Override
    public void generateDefaults() {
        set("config.enableSafeSearch", true,
            "Whether to search for any nearby safe teleport locations if the original back location isn't safe to teleport to.",
            "This can be useful if a player falls into the void, falls into lava, or their back location is obstructed in some way.");
        set("config.safeCheckRadius", 5, "Radius of search when back location isn't safe to teleport to.");
        set("config.safeCheckHeight", 50,
            "Max height of search when back location isn't safe to teleport to.",
            "Useful for when the player jumps into lava or the void.");
        setComments("config", "Configuration options for the back module.");
        set("locations", Map.of(), "All back locations.", "DO NOT MODIFY THIS UNLESS YOU KNOW WHAT YOU'RE DOING!");
    }

}
