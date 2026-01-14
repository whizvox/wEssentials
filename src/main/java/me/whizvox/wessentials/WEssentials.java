package me.whizvox.wessentials;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.whizvox.wessentials.module.NicknameModule;
import me.whizvox.wessentials.module.teleport.TeleportRequestModule;
import me.whizvox.wessentials.module.warp.WarpModule;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class WEssentials extends JavaPlugin {

    private final Messages messages;
    private final TeleportRequestModule teleports;
    private final WarpModule warps;
    private final NicknameModule nicknames;

    public WEssentials() {
        instance = this;
        messages = new Messages();
        teleports = new TeleportRequestModule();
        warps = new WarpModule();
        nicknames = new NicknameModule();
    }

    public void reload() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().log(Level.SEVERE, "Could not create data folder at {}", getDataFolder());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        Configuration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        messages.load(messagesConfig);
        File warpsFile = new File(getDataFolder(), "warps.yml");
        if (warpsFile.exists()) {
            Configuration warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
            warps.load(warpsConfig);
        }
        File nicknamesFile = new File(getDataFolder(), "nicknames.yml");
        if (nicknamesFile.exists()) {
            Configuration nicknamesConfig = YamlConfiguration.loadConfiguration(nicknamesFile);
            nicknames.load(nicknamesConfig);
        }
    }

    @Override
    public void onEnable() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
            commands -> WEssentialsCommands.registerAll(commands.registrar()));
        getServer().getAsyncScheduler().runAtFixedRate(this, $ -> teleports.removeInvalid(), 1000, 10, TimeUnit.SECONDS);
        reload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Messages getMessages() {
        return messages;
    }

    public TeleportRequestModule getTeleports() {
        return teleports;
    }

    public WarpModule getWarps() {
        return warps;
    }

    public NicknameModule getNicknames() {
        return nicknames;
    }

    public void saveWarps() {
        File warpsFile = new File(getDataFolder(), "warps.yml");
        FileConfiguration warpsConfig = new YamlConfiguration();
        warps.save(warpsConfig);
        try {
            warpsConfig.save(warpsFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save warps file: " + warpsFile, e);
        }
    }

    public void saveNicknames() {
        File file = new File(getDataFolder(), "nicknames.yml");
        FileConfiguration config = new YamlConfiguration();
        nicknames.save(config);
        try {
            config.save(file);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save nicknames file: " + file, e);
        }
    }

    private static WEssentials instance = null;

    public static WEssentials inst() {
        return instance;
    }

    public static Component translate(String key, Map<String, Object> args) {
        return instance.messages.translate(key, args);
    }

    public static Component translate(String key) {
        return instance.messages.translate(key);
    }

}
