package me.whizvox.wessentials;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to generate clean, organized configuration files with default values.
 */
public abstract class ConfigurationGenerator {

    private final Configuration oldConfig;
    private final FileConfiguration newConfig;

    public ConfigurationGenerator(Configuration config) {
        oldConfig = config;
        newConfig = new YamlConfiguration();
    }

    public List<String> findOrphanedPaths() {
        List<String> paths = new ArrayList<>();
        oldConfig.getKeys(true).forEach(path -> {
            if (!newConfig.contains(path)) {
                paths.add(path);
            }
        });
        return paths;
    }

    public FileConfiguration getNewConfiguration() {
        return newConfig;
    }

    protected void setComments(String path, String... comments) {
        if (comments.length == 0) {
            newConfig.setComments(path, null);
        } else {
            newConfig.setComments(path, List.of(comments));
        }
    }

    protected void set(String path, Object value, String... comments) {
        if (oldConfig.contains(path)) {
            newConfig.set(path, oldConfig.get(path));
        } else {
            newConfig.set(path, value);
        }
        setComments(path, comments);
    }

    protected void set(String path, List<String> oldPaths, Object value, String... comments) {
        if (oldConfig.contains(path)) {
            newConfig.set(path, oldConfig.get(path));
        } else {
            oldPaths.stream().filter(oldConfig::contains).findFirst().ifPresentOrElse(
                oldPath -> newConfig.set(path, oldConfig.get(oldPath)),
                () -> newConfig.set(path, value)
            );
        }
        setComments(path, comments);
    }

    /**
     * Set default values if they do not exist, and update comments of all those values if wanted.
     * @see #set(String, Object, String...)
     * @see #set(String, List, Object, String...)
     */
    public abstract void generateDefaults();

}
