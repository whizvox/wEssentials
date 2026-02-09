package me.whizvox.wessentials.module;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import static me.whizvox.wessentials.util.StringUtil.logParamsException;

@NotNullByDefault
public class ModuleManager {

    private final Plugin plugin;
    private final Map<String, me.whizvox.wessentials.module.Module> nameMap;
    private final Map<Class<?>, me.whizvox.wessentials.module.Module> classMap;

    public ModuleManager(Plugin plugin) {
        this.plugin = plugin;
        nameMap = new Object2ObjectOpenHashMap<>();
        classMap = new Object2ObjectOpenHashMap<>();
    }

    public void register(me.whizvox.wessentials.module.Module module) {
        nameMap.put(module.getName(), module);
        classMap.put(module.getClass(), module);
    }

    public void registerAll(me.whizvox.wessentials.module.Module... modules) {
        for (me.whizvox.wessentials.module.Module module : modules) {
            register(module);
        }
    }

    public <T extends me.whizvox.wessentials.module.Module> Optional<T> getModule(Class<T> type) {
        me.whizvox.wessentials.module.Module module = classMap.get(type);
        if (module != null && type.isAssignableFrom(module.getClass())) {
            return Optional.of(type.cast(module));
        }
        return Optional.empty();
    }

    public Optional<Module> getModule(String name) {
        return Optional.ofNullable(nameMap.get(name));
    }

    public void load(Configuration config) {
        ConfigurationSection modulesSec = config.getConfigurationSection("module");
        if (modulesSec != null) {
            modulesSec.getKeys(false).forEach(name -> getModule(name).ifPresentOrElse(
                module -> {
                    if (modulesSec.getBoolean(name)) {
                        module.setEnabled(true);
                    }
                },
                () -> plugin.getLogger().log(Level.WARNING, "Unknown module in configuration file: '{0}'", name)
            ));
        }
        nameMap.forEach((name, module) -> {
            if (modulesSec == null || !modulesSec.contains(name)) {
                plugin.getLogger().log(Level.WARNING, "Module '{0}' not found in configuration.", name);
            }
        });
    }

    public void loadModules() {
        nameMap.values().forEach(module -> {
            if (module.isEnabled()) {
                plugin.getLogger().log(Level.INFO, "Loading module ''{0}''", module.getName());
                try {
                    module.load();
                } catch (Exception e) {
                    plugin.getLogger().log(logParamsException(Level.WARNING, e, "Failed to load module ''{0}''. This will very likely result in adverse effects!", module.getName()));
                }
            } else {
                plugin.getLogger().log(Level.INFO, "Module ''{0}'' is disabled. Will not load.", module.getName());
            }
        });
    }

    public void saveModules() {
        nameMap.values().forEach(module -> {
            if (module.isEnabled()) {
                plugin.getLogger().info("Saving module '" + module.getName() + "'");
                try {
                    module.save();
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Failed to save module '" + module.getName() + "'. This will very likely result in adverse effects!", e);
                }
            }
        });
    }

}
