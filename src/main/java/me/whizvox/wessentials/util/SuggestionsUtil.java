package me.whizvox.wessentials.util;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.module.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class SuggestionsUtil {

    public static SuggestionProvider<CommandSourceStack> ONLINE_PLAYERS = (context, builder) -> {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getName().toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(player.getName());
            }
        });
        return builder.buildFuture();
    };

    public static SuggestionProvider<CommandSourceStack> WARPS = (context, builder) -> {
        WEssentials.inst().getWarps().getWarpNames().stream()
            .sorted()
            .forEach(name -> {
                if (name.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                    builder.suggest(name);
                }
            });
        return builder.buildFuture();
    };

    public static void kitsOrAll(SuggestionsBuilder builder) {
        if ("all".startsWith(builder.getRemainingLowerCase())) {
            builder.suggest("all");
        }
        WEssentials.inst().getKits().getAll().stream()
            .filter(kit -> kit.name().toLowerCase().startsWith(builder.getRemainingLowerCase()))
            .sorted(Comparator.comparing(Kit::name))
            .forEach(kit -> builder.suggest(kit.name()));
    };

    public static void customTextKeys(SuggestionsBuilder builder) {
        WEssentials.inst().getCustomText().getKeys().stream()
            .filter(key -> key.toLowerCase().startsWith(builder.getRemainingLowerCase()))
            .sorted(String::compareTo)
            .forEach(builder::suggest);
    }

}
