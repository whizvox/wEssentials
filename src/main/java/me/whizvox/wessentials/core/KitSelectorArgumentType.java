package me.whizvox.wessentials.core;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.exception.WECommandExceptions;
import me.whizvox.wessentials.module.kit.Kit;
import me.whizvox.wessentials.module.kit.KitSelectorResolver;
import me.whizvox.wessentials.util.SuggestionsUtil;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@NotNullByDefault
public class KitSelectorArgumentType implements CustomArgumentType.Converted<KitSelectorResolver, String> {

    private KitSelectorArgumentType() {
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        SuggestionsUtil.kitsOrAll(builder);
        return builder.buildFuture();
    }

    @Override
    public KitSelectorResolver convert(String name) throws CommandSyntaxException {
        if (name.equals("all")) {
            return () -> WEssentials.inst().getKits().getAll().stream().toList();
        }
        Kit kit = WEssentials.inst().getKits().get(name);
        if (kit == null) {
            throw WECommandExceptions.UNKNOWN_KIT.create(name);
        }
        return () -> List.of(kit);
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    public static KitSelectorArgumentType kits() {
        return new KitSelectorArgumentType();
    }

    public static KitSelectorResolver getKits(CommandContext<?> context, String name) {
        return context.getArgument(name, KitSelectorResolver.class);
    }

}
