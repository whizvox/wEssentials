package me.whizvox.wessentials;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.whizvox.wessentials.module.BackModule;
import me.whizvox.wessentials.module.chat.ChatModule;
import me.whizvox.wessentials.module.chat.EmptyPrefixSuffixProvider;
import me.whizvox.wessentials.module.chat.LuckPermsPrefixSuffixProvider;
import me.whizvox.wessentials.module.customtext.CustomTextModule;
import me.whizvox.wessentials.module.home.Home;
import me.whizvox.wessentials.module.home.HomeModule;
import me.whizvox.wessentials.module.kit.Kit;
import me.whizvox.wessentials.module.kit.KitCooldown;
import me.whizvox.wessentials.module.kit.KitModule;
import me.whizvox.wessentials.module.kit.SlottedItem;
import me.whizvox.wessentials.module.nick.Nickname;
import me.whizvox.wessentials.module.nick.NicknameModule;
import me.whizvox.wessentials.module.teleport.TeleportRequestModule;
import me.whizvox.wessentials.module.warp.WarpModule;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
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
    private ChatModule chat;
    private final HomeModule homes;
    private final KitModule kits;
    private final BackModule back;
    private final CustomTextModule customText;

    public WEssentials() {
        instance = this;
        messages = new Messages();
        teleports = new TeleportRequestModule();
        warps = new WarpModule();
        nicknames = new NicknameModule();
        chat = null;
        homes = new HomeModule(this);
        kits = new KitModule(this);
        back = new BackModule(this);
        customText = new CustomTextModule(this);
    }

    public void reload() {
        // Data folder
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().log(Level.SEVERE, "Could not create data folder at {}", getDataFolder());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        // Messages
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        Configuration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        messages.load(messagesConfig);
        // Chat
        File chatFile = new File(getDataFolder(), "chat.yml");
        if (!chatFile.exists()) {
            saveResource("chat.yml", false);
        }
        Configuration chatConfig = YamlConfiguration.loadConfiguration(chatFile);
        chat.load(chatConfig);
        // Warps
        File warpsFile = new File(getDataFolder(), "warps.yml");
        if (warpsFile.exists()) {
            Configuration warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
            warps.load(warpsConfig);
        }
        // Nicknames
        File nicknamesFile = new File(getDataFolder(), "nicknames.yml");
        if (nicknamesFile.exists()) {
            Configuration nicknamesConfig = YamlConfiguration.loadConfiguration(nicknamesFile);
            nicknames.load(nicknamesConfig);
        }
        // Homes
        homes.load();
        // Kits
        kits.load();
        // Back
        back.load();
        // Custom text
        customText.load();
    }

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(Nickname.class);
        ConfigurationSerialization.registerClass(Home.class);
        ConfigurationSerialization.registerClass(SlottedItem.class);
        ConfigurationSerialization.registerClass(Kit.class);
        ConfigurationSerialization.registerClass(KitCooldown.class);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();
            WEssentialsCommands.registerAll(commands);
            customText.registerCommands(commands);
        });
        getServer().getAsyncScheduler().runAtFixedRate(this, $ -> teleports.removeInvalid(), 1000, 10, TimeUnit.SECONDS);
        getServer().getPluginManager().registerEvents(new WEssentialsEventListener(), this);
        if (getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            try {
                chat = new ChatModule(new LuckPermsPrefixSuffixProvider());
                getLogger().info("Loaded LuckPerms prefix/suffix provider");
            } catch (Exception e) {
                chat = new ChatModule(new EmptyPrefixSuffixProvider());
                getLogger().log(Level.SEVERE, "Could not load LuckPerms prefix/suffix provider", e);
            }
        } else {
            chat = new ChatModule(new EmptyPrefixSuffixProvider());
        }
        getServer().getPluginManager().registerEvents(chat, this);
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

    public HomeModule getHomes() {
        return homes;
    }

    public KitModule getKits() {
        return kits;
    }

    public BackModule getBack() {
        return back;
    }

    public CustomTextModule getCustomText() {
        return customText;
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
