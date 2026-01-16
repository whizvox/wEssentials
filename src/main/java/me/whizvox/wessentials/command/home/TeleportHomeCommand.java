package me.whizvox.wessentials.command.home;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TeleportHomeCommand extends ModuleCommand {

    private final Permission otherPermission;

    public TeleportHomeCommand() {
        super("home", "home", "home.teleport");
        otherPermission = new Permission("wessentials.home.teleport.other");
    }

    private int teleportToHome(Player sender, @Nullable Player receiver) {
        if (receiver == null) {
            receiver = sender;
        }
        Location location = WEssentials.inst().getHomes().getHome(receiver);
        if (location != null) {
            if (sender.hasPermission(WEssentials.inst().getBack().homePermission)) {
                WEssentials.inst().getBack().setLocation(sender, sender.getLocation());
                WEssentials.inst().getBack().save();
            }
            sender.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
            if (sender == receiver) {
                sender.sendMessage(WEssentials.translate("message.home.teleport"));
            } else {
                sender.sendMessage(WEssentials.translate("message.home.teleportOther", Map.of("player", receiver.displayName())));
            }
        } else {
            sender.sendMessage(WEssentials.translate("message.home.notSet"));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean hasPermission(CommandSourceStack source) {
        return super.hasPermission(source) && source.getSender() instanceof Player;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.argument("player", ArgumentTypes.player())
                .requires(source -> source.getSender().hasPermission(otherPermission))
                .executes(context -> teleportToHome(
                    (Player) context.getSource().getSender(),
                    context.getArgument("player", PlayerSelectorArgumentResolver.class)
                        .resolve(context.getSource())
                        .getFirst()
                ))
            )
            .executes(context -> teleportToHome((Player) context.getSource().getSender(), null));
    }

}
