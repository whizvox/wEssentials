package me.whizvox.wessentials;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.whizvox.wessentials.module.BackModule;
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
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

        // Modules
        chat.load();
        warps.load();
        nicknames.load();
        homes.load();
        kits.load();
        back.load();
        customText.load();
    }

    @Override
    public void onEnable() {
        // register custom configuration types
        ConfigurationSerialization.registerClass(Nickname.class);
        ConfigurationSerialization.registerClass(Home.class);
        ConfigurationSerialization.registerClass(SlottedItem.class);
        ConfigurationSerialization.registerClass(Kit.class);
        ConfigurationSerialization.registerClass(KitCooldown.class);

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

        // register events
        PluginManager pluginMan = getServer().getPluginManager();
        pluginMan.registerEvents(back, this);
        pluginMan.registerEvents(chat, this);
        pluginMan.registerEvents(nicknames, this);
        pluginMan.registerEvents(teleports, this);

        // soft reload
        reload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Messages getMessages() {
        return messages;
    }

    // Modules

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
