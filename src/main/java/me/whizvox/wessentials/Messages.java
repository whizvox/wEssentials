package me.whizvox.wessentials;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.Argument;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import org.bukkit.configuration.Configuration;

import java.util.*;

public class Messages {

    private Locale locale;
    private MiniMessageTranslationStore store;
    private final Map<String, String> plainMessages;

    public Messages() {
        locale = Locale.US;
        store = MiniMessageTranslationStore.create(Key.key("wessentials"));
        plainMessages = new Object2ObjectOpenHashMap<>();
    }

    public void load(Configuration config) {
        store = MiniMessageTranslationStore.create(Key.key("wessentials"));
        plainMessages.clear();
        config.getKeys(true).forEach(key -> {
            if (key.equals("locale")) {
                locale = Locale.of(config.getString("locale", "en-US"));
            } else if (key.startsWith("message.")) {
                store.register(key, locale, config.getString(key));
            } else if (key.startsWith("exception.")) {
                plainMessages.put(key, config.getString(key));
            }
        });
    }

    public Component translate(TranslatableComponent component) {
        Component result = store.translate(component, locale);
        return Objects.requireNonNullElse(result,
            MiniMessage.miniMessage().deserialize("<bold><dark_red>[!]</dark_red></bold> <red>" + component.key() + "</red>"));
    }

    @SuppressWarnings("PatternValidation")
    public Component translate(String key, Map<String, Object> args) {
        List<ComponentLike> arguments = new ArrayList<>(args.size());
        args.forEach((argKey, value) -> {
            if (value instanceof ComponentLike compLike) {
                arguments.add(Argument.component(argKey, compLike));
            } else {
                arguments.add(Argument.component(argKey, Component.text(String.valueOf(value))));
            }
        });
        TranslatableComponent tlComp = Component.translatable(key).arguments(arguments);
        return translate(tlComp);
    }

    public Component translate(String key) {
        TranslatableComponent tlComp = Component.translatable(key);
        return translate(tlComp);
    }

    public String translatePlain(String key, Object... args) {
        String message = plainMessages.get(key);
        if (message == null) {
            return "[!] " + key;
        }
        return message.formatted(args);
    }

}
