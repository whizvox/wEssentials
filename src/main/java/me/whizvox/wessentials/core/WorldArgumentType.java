package me.whizvox.wessentials.core;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.whizvox.wessentials.exception.WECommandExceptions;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.concurrent.CompletableFuture;

@NotNullByDefault
public class WorldArgumentType implements CustomArgumentType.Converted<World, String> {

    private WorldArgumentType() {
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Bukkit.getWorlds().forEach(world -> {
            if (world.getName().toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(world.getName());
            }
        });
        return builder.buildFuture();
    }

    @Override
    public World convert(String name) throws CommandSyntaxException {
        World world = Bukkit.getWorld(name);
        if (world == null) {
            throw WECommandExceptions.UNKNOWN_WORLD.create(name);
        }
        return world;
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public static WorldArgumentType world() {
        return new WorldArgumentType();
    }

    public static World getWorld(CommandContext<?> context, String name) {
        return context.getArgument(name, World.class);
    }

}
