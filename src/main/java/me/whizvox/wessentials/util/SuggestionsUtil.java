package me.whizvox.wessentials.util;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.whizvox.wessentials.WEssentials;
import org.bukkit.Bukkit;

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

}
