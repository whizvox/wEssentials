package me.whizvox.wessentials.module;

import me.whizvox.wessentials.ConfigurationGenerator;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@NotNullByDefault
public abstract class SerializableModule extends SimpleModule {

    private final Plugin plugin;
    private final String configFileName;
    private final boolean saveIfNotExist;
    protected @Nullable File file;

    public SerializableModule(String name, Plugin plugin, String configFileName, boolean saveIfNotExist) {
        super(name);
        this.plugin = plugin;
        this.configFileName = configFileName;
        this.saveIfNotExist = saveIfNotExist;
        file = null;
    }

    private void initializeFile() {
        if (file == null) {
            file = new File(plugin.getDataFolder(), configFileName);
        }
    }

    @Override
    public void load() {
        initializeFile();
        Configuration config;
        //noinspection DataFlowIssue
        if (!file.exists()) {
            if (saveIfNotExist) {
                plugin.saveResource(configFileName, false);
                config = YamlConfiguration.loadConfiguration(file);
            } else {
                config = new MemoryConfiguration();
            }
        } else {
            config = YamlConfiguration.loadConfiguration(file);
        }
        ConfigurationGenerator gen = getConfigurationGenerator(config);
        if (gen != null) {
            gen.generateDefaults();
            config = gen.getNewConfiguration();
        }
        loadFrom(config);
        if (gen != null) {
            try {
                gen.getNewConfiguration().save(file);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not save module configuration file: " + file, e);
            }
        }
    }

    @Override
    public void save() {
        initializeFile();
        FileConfiguration config = new YamlConfiguration();
        saveTo(config);
        ConfigurationGenerator gen = getConfigurationGenerator(config);
        if (gen != null) {
            gen.generateDefaults();
            config = gen.getNewConfiguration();
        }
        try {
            //noinspection DataFlowIssue
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save configuration file: " + file, e);
        }
    }

    public @Nullable ConfigurationGenerator getConfigurationGenerator(Configuration config) {
        return null;
    }

    protected abstract void loadFrom(Configuration config);

    protected abstract void saveTo(Configuration config);

}
