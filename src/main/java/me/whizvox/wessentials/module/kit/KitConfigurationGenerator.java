package me.whizvox.wessentials.module.kit;

import me.whizvox.wessentials.ConfigurationGenerator;
import org.bukkit.configuration.Configuration;

import java.util.List;

public class KitConfigurationGenerator extends ConfigurationGenerator {

    public KitConfigurationGenerator(Configuration config) {
        super(config);
    }

    @Override
    public void generateDefaults() {
        set("dropIfFull", false,
            "Any items from a kit which cannot fit into a player's inventory will be...",
            "- true: dropped on the ground.",
            "- false: ignored and destroyed.",
            "You might want to consider disabling this if you are concerned about potential lag abuse."
        );
        set("kits", (Object) List.of(),
            "List of all kits.",
            "Do NOT change this unless you know what you're doing!"
        );
        set("cooldowns", (Object) List.of(),
            "List of all kit cooldowns.",
            "Do NOT change this unless you know what you're doing!"
        );
    }

}
