package me.whizvox.wessentials;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.whizvox.wessentials.module.ModuleManager;
import me.whizvox.wessentials.module.back.BackLocation;
import me.whizvox.wessentials.module.back.BackModule;
import me.whizvox.wessentials.module.chat.ChatModule;
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
import org.bukkit.plugin.PluginManager;
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
    private final ChatModule chat;
    private final HomeModule homes;
    private final KitModule kits;
    private final BackModule back;
    private final CustomTextModule customText;
    private final ModuleManager modules;

    public WEssentials() {
        instance = this;
        messages = new Messages();
        teleports = new TeleportRequestModule();
        warps = new WarpModule(this);
        nicknames = new NicknameModule(this);
        chat = new ChatModule(this);
        homes = new HomeModule(this);
        kits = new KitModule(this);
        back = new BackModule(this);
        customText = new CustomTextModule(this);
        modules = new ModuleManager(this);
        modules.registerAll(teleports, warps, nicknames, chat, homes, kits, back, customText);
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
        DefaultMessages defaultMessages = new DefaultMessages(messagesConfig);
        defaultMessages.generateDefaults();
        FileConfiguration newMessagesConfig = defaultMessages.getNewConfiguration();
        messages.load(newMessagesConfig);
        try {
            newMessagesConfig.save(messagesFile);
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Could not save messages at " + messagesFile, e);
        }

        // Modules
        modules.loadModules();
    }

    @Override
    public void onEnable() {
        // register custom configuration types
        ConfigurationSerialization.registerClass(Nickname.class);
        ConfigurationSerialization.registerClass(Home.class);
        ConfigurationSerialization.registerClass(SlottedItem.class);
        ConfigurationSerialization.registerClass(Kit.class);
        ConfigurationSerialization.registerClass(KitCooldown.class);
        ConfigurationSerialization.registerClass(BackLocation.class);

        // register commands
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();
            WEssentialsCommands.registerAll(commands);
            customText.registerCommands(commands);
        });

        // schedule periodic cleaning of teleport requests
        getServer().getAsyncScheduler().runAtFixedRate(this, $ -> teleports.removeInvalid(), 1000, 10, TimeUnit.SECONDS);

        // attempt to load LuckPerms prefix/suffix provider
        if (getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            try {
                chat.setPrefixSuffixProvider(new LuckPermsPrefixSuffixProvider());
                getLogger().info("Loaded LuckPerms prefix/suffix provider");
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Could not load LuckPerms prefix/suffix provider", e);
            }
        }

        getLogger().info("Loading main configuration");
        PluginConfigurationGenerator pluginConfigGen = new PluginConfigurationGenerator(getConfig());
        pluginConfigGen.generateDefaults();
        FileConfiguration newPluginConfig = pluginConfigGen.getNewConfiguration();
        modules.load(newPluginConfig);
        try {
            newPluginConfig.save(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Could not save plugin configuration file", e);
        }

        // register events
        PluginManager pluginMan = getServer().getPluginManager();
        if (back.isEnabled()) {
            pluginMan.registerEvents(back, this);
        }
        if (chat.isEnabled()) {
            pluginMan.registerEvents(chat, this);
        }
        if (nicknames.isEnabled()) {
            pluginMan.registerEvents(nicknames, this);
        }
        if (teleports.isEnabled()) {
            pluginMan.registerEvents(teleports, this);
        }

        // soft reload
        reload();
    }

    @Override
    public void onDisable() {
        modules.saveModules();
    }

    public Messages getMessages() {
        return messages;
    }

    // Modules

    public ModuleManager getModules() {
        return modules;
    }

    public BackModule getBack() {
        return back;
    }

    public CustomTextModule getCustomText() {
        return customText;
    }

    public HomeModule getHomes() {
        return homes;
    }

    public KitModule getKits() {
        return kits;
    }

    public NicknameModule getNicknames() {
        return nicknames;
    }

    public TeleportRequestModule getTeleports() {
        return teleports;
    }

    public WarpModule getWarps() {
        return warps;
    }

    // Singleton logic

    private static WEssentials instance = null;

    public static WEssentials inst() {
        return instance;
    }

    // Helper translation methods

    public static Component translate(String key, Map<String, Object> args) {
        return instance.messages.translate(key, args);
    }

    public static Component translate(String key) {
        return instance.messages.translate(key);
    }

}
