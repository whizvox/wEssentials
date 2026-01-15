package me.whizvox.wessentials.module;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@NotNullByDefault
public abstract class SerializableModule {

    private final Plugin plugin;
    private final String configFileName;
    private final boolean saveIfNotExist;
    private boolean firstLoad;
    protected @Nullable File file;

    public SerializableModule(Plugin plugin, String configFileName, boolean saveIfNotExist) {
        this.plugin = plugin;
        this.configFileName = configFileName;
        this.saveIfNotExist = saveIfNotExist;
        firstLoad = true;
        file = null;
    }

    private void initializeFile() {
        if (file == null) {
            file = new File(plugin.getDataFolder(), configFileName);
        }
    }

    public void load() {
        if (firstLoad) {
            firstLoad = false;
            if (this instanceof Listener listener) {
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
            }
        }
        initializeFile();
        //noinspection DataFlowIssue
        if (!file.exists()) {
            if (saveIfNotExist) {
                plugin.saveResource(configFileName, false);
            } else {
                return;
            }
        }
        Configuration config = YamlConfiguration.loadConfiguration(file);
        loadFrom(config);
    }

    @SuppressWarnings("DataFlowIssue")
    public void save() {
        initializeFile();
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        saveTo(config);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save configuration file: " + file, e);
        }
    }

    protected abstract void loadFrom(Configuration config);

    protected abstract void saveTo(Configuration config);

}
