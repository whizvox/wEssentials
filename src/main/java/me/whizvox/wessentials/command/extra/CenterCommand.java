package me.whizvox.wessentials.command.extra;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.RotationResolver;
import io.papermc.paper.math.Rotation;
import me.whizvox.wessentials.core.ModuleCommand;
import me.whizvox.wessentials.exception.WECommandExceptions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

public class CenterCommand extends ModuleCommand {

    public CenterCommand() {
        super(null, "center");
    }

    @Override
    public boolean hasPermission(CommandSourceStack source) {
        return super.hasPermission(source) && source.getSender() instanceof Player;
    }

    private int center(Player player, @Nullable Rotation rotation) {
        Location center = player.getLocation().toCenterLocation();
        center.setY(center.getBlockY());
        if (rotation != null) {
            center.setRotation(rotation);
        }
        player.teleport(center, PlayerTeleportEvent.TeleportCause.COMMAND);
        return Command.SINGLE_SUCCESS;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.literal("face")
                .then(Commands.argument("face", StringArgumentType.word())
                    .suggests((context, sBuilder) -> {
                        for (Face face : Face.values()) {
                            if (face.toString().toLowerCase().startsWith(sBuilder.getRemainingLowerCase())) {
                                sBuilder.suggest(face.toString().toLowerCase());
                            }
                        }
                        return sBuilder.buildFuture();
                    })
                    .executes(context -> {
                        String faceStr = StringArgumentType.getString(context, "face");
                        try {
                            Face face = Face.fromName(faceStr);
                            return center((Player) context.getSource().getSender(), face.rotation);
                        } catch (IllegalArgumentException e) {
                            throw WECommandExceptions.INVALID_FACE.create(faceStr);
                        }
                    })
                )
            )
            .then(Commands.literal("rotation")
                .then(Commands.argument("rotation", ArgumentTypes.rotation())
                    .executes(context -> center(
                        (Player) context.getSource().getSender(),
                        context.getArgument("rotation", RotationResolver.class).resolve(context.getSource())
                    ))
                )
            )
            .executes(context -> center((Player) context.getSource().getSender(), null));
    }

    private enum Face {
        NORTH(Rotation.rotation(180, 0)),
        SOUTH(Rotation.rotation(0, 0)),
        EAST(Rotation.rotation(-90, 0)),
        WEST(Rotation.rotation(90, 0)),
        UP(Rotation.rotation(0, -90)),
        DOWN(Rotation.rotation(0, 90));

        public final Rotation rotation;
        public final String name;

        Face(Rotation rotation) {
            this.rotation = rotation;
            name = toString().toLowerCase();
        }

        public String getName() {
            return name;
        }

        public static Face fromName(String name) {
            return switch (name) {
                case "north" -> NORTH;
                case "south" -> SOUTH;
                case "east" -> EAST;
                case "west" -> WEST;
                case "up" -> UP;
                case "down" -> DOWN;
                default -> throw new IllegalArgumentException("Unknown face: " + name);
            };
        }

    }

}
