package me.whizvox.wessentials.command.warp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.RotationResolver;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import me.whizvox.wessentials.core.WorldArgumentType;
import me.whizvox.wessentials.exception.WECommandExceptions;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Pattern;

public class SetWarpCommand extends ModuleCommand {

    private static final Pattern NAME_PATTERN = Pattern.compile("\\w{1,32}");

    public SetWarpCommand() {
        super("warp", "setwarp", "warp.set");
    }

    private int setWarp(CommandSender sender, String name, Location location) throws CommandSyntaxException {
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw WECommandExceptions.INVALID_WARP_NAME.create(name);
        }
        //Location other = WEssentials.inst().getWarps().getWarp(name);
        //if (other == null) {
            WEssentials.inst().getWarps().set(name, location);
            WEssentials.inst().getWarps().save();
            sender.sendMessage(WEssentials.translate("message.warp.set", Map.of("warp", name)));
        //} else {
        //    sender.sendMessage(WEssentials.translate("message.warp.nameConflict", Map.of("warp", name)));
        //}
        return Command.SINGLE_SUCCESS;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        //noinspection UnstableApiUsage
        builder.then(Commands.argument("name", StringArgumentType.word())
            .then(Commands.argument("position", ArgumentTypes.finePosition())
                .then(Commands.argument("world", WorldArgumentType.world())
                    .then(Commands.argument("rotation", ArgumentTypes.rotation())
                        .executes(context -> setWarp(
                            context.getSource().getSender(),
                            StringArgumentType.getString(context, "name"),
                            context.getArgument("position", FinePositionResolver.class)
                                .resolve(context.getSource())
                                .toLocation(WorldArgumentType.getWorld(context, "world"))
                                .setRotation(context.getArgument("rotation", RotationResolver.class)
                                    .resolve(context.getSource()))
                        ))
                    )
                    .executes(context -> setWarp(
                        context.getSource().getSender(),
                        StringArgumentType.getString(context, "name"),
                        context.getArgument("position", FinePositionResolver.class)
                            .resolve(context.getSource())
                            .toLocation(WorldArgumentType.getWorld(context, "world"))
                    ))
                )
                .requires(source -> source.getSender() instanceof Player)
                .executes(context -> setWarp(
                    context.getSource().getSender(),
                    StringArgumentType.getString(context, "name"),
                    context.getArgument("position", FinePositionResolver.class)
                        .resolve(context.getSource())
                        .toLocation(((Player) context.getSource().getSender()).getWorld())
                ))
            )
            .requires(source -> source.getSender() instanceof Player)
            .executes(context -> setWarp(
                context.getSource().getSender(),
                StringArgumentType.getString(context, "name"),
                ((Player) context.getSource().getSender()).getLocation()
            ))
        );
    }

}
