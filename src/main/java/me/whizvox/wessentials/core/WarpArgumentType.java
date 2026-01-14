package me.whizvox.wessentials.core;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.exception.WECommandExceptions;
import me.whizvox.wessentials.module.warp.WarpLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.concurrent.CompletableFuture;

@NotNullByDefault
public class WarpArgumentType implements CustomArgumentType.Converted<WarpLocation, String> {

    private WarpArgumentType() {
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        WEssentials.inst().getWarps().getWarpNames().stream()
            .sorted()
            .forEach(name -> {
                if (name.toLowerCase().startsWith(builder.getRemainingLowerCase()) && context.getSource() instanceof CommandSourceStack source && source.getSender().hasPermission("wessentials.warp." + name)) {
                    builder.suggest(name);
                }
            });
        return builder.buildFuture();
    }

    @Override
    public WarpLocation convert(String name) throws CommandSyntaxException {
        Location loc = WEssentials.inst().getWarps().getWarp(name);
        if (loc == null) {
            throw WECommandExceptions.WARP_NOT_FOUND.create(name);
        }
        return new WarpLocation(name, loc);
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public static WarpArgumentType warp() {
        return new WarpArgumentType();
    }

    public static WarpLocation getWarp(CommandContext<?> context, String name) {
        return context.getArgument(name, WarpLocation.class);
    }

}
