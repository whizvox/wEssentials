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
import me.whizvox.wessentials.module.kit.Kit;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

@NotNullByDefault
public class KitArgumentType implements CustomArgumentType.Converted<Kit, String> {

    private KitArgumentType() {
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        WEssentials.inst().getKits().getAll().stream()
            .filter(kit -> kit.name().toLowerCase().startsWith(builder.getRemainingLowerCase()) &&
                (!(context.getSource() instanceof CommandSourceStack source) ||
                    source.getSender().hasPermission(kit.getPermission())))
            .sorted(Comparator.comparing(Kit::name))
            .forEach(kit -> builder.suggest(kit.name()));
        return builder.buildFuture();
    }

    @Override
    public Kit convert(String name) throws CommandSyntaxException {
        Kit kit = WEssentials.inst().getKits().get(name);
        if (kit == null) {
            throw WECommandExceptions.UNKNOWN_KIT.create(name);
        }
        return kit;
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public static KitArgumentType kit() {
        return new KitArgumentType();
    }

    public static Kit getKit(CommandContext<?> context, String name) {
        return context.getArgument(name, Kit.class);
    }

}
