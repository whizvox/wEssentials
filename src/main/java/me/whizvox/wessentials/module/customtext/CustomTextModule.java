package me.whizvox.wessentials.module.customtext;

import io.papermc.paper.command.brigadier.Commands;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.command.customtext.ShowCustomTextCommand;
import me.whizvox.wessentials.module.SerializableModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@NotNullByDefault
public class CustomTextModule extends SerializableModule {

    private final Map<String, List<String>> text;
    private final Map<String, Component> textCache;

    public CustomTextModule(Plugin plugin) {
        super(plugin, "customtext.yml", false);
        text = new Object2ObjectOpenHashMap<>();
        textCache = new Object2ObjectOpenHashMap<>();
    }

    @Override
    protected void loadFrom(Configuration config) {
        text.clear();
        textCache.clear();
        Map<String, Object> values = config.getValues(false);
        values.forEach((key, textObj) -> {
            //noinspection unchecked
            List<String> lines = (List<String>) textObj;
            if (!lines.isEmpty()) {
                setText(key, lines);
            }
        });
    }

    @Override
    protected void saveTo(Configuration config) {
        text.forEach(config::set);
    }

    public @Nullable Component getMessage(String key) {
        return textCache.get(key);
    }

    public @Nullable List<String> getRawText(String key) {
        return Collections.unmodifiableList(text.get(key));
    }

    public Collection<String> getKeys() {
        return text.keySet();
    }

    public boolean setText(String key, List<String> lines) {
        boolean requiresRestart = !text.containsKey(key);
        text.put(key, lines);
        Component comp = Component.text("");
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) {
                comp = comp.append(Component.newline());
            }
            comp = comp.append(MiniMessage.miniMessage().deserialize(lines.get(i)));
        }
        textCache.put(key, comp);
        return requiresRestart;
    }

    public void registerCommands(Commands commands) {
        text.keySet().forEach(key -> {
            ShowCustomTextCommand command = new ShowCustomTextCommand(key, this);
            command.register(commands);
            WEssentials.inst().getLogger().info("Registered custom text command: /" + key);
        });
    }

    public static String getPermission(String key) {
        return "wessentials.customtext.message." + key;
    }

}
