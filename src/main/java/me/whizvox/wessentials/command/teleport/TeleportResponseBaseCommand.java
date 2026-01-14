package me.whizvox.wessentials.command.tp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

public class TeleportResponseBaseCommand extends ModuleCommand {

    private final boolean accept;

    public TeleportResponseBaseCommand(String command, boolean accept) {
        super("teleport", command, (Permission) null);
        this.accept = accept;
    }

    private int respond(CommandSender sender, @Nullable Player tpSender) {
        if (accept) {
            WEssentials.inst().getTeleports().accept((Player) sender, tpSender);
        } else {
            WEssentials.inst().getTeleports().deny((Player) sender, tpSender);
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
                .executes(context -> respond(
                    context.getSource().getSender(),
                    context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource()).getFirst()
                ))
            )
            .executes(context -> respond(context.getSource().getSender(), null));
    }

}
