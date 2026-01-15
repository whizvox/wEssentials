package me.whizvox.wessentials.command.home;

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

import java.util.Map;

public class ClearHomeCommand extends ModuleCommand {

    private final Permission otherPermission;

    public ClearHomeCommand() {
        super("home", "clearhome", "home.clear");
        otherPermission = new Permission("wessentials.command.home.clear.other");
    }

    private int clearHome(CommandSender sender, @Nullable Player receiver) {
        if (receiver == null) {
            receiver = (Player) sender;
        }
        WEssentials.inst().getHomes().clearHome(receiver);
        WEssentials.inst().getHomes().save();
        receiver.sendMessage(WEssentials.translate("message.home.clear"));
        if (sender != receiver) {
            sender.sendMessage(WEssentials.translate("message.home.clearOther", Map.of("player", receiver.displayName())));
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
                .executes(context -> clearHome(
                    context.getSource().getSender(),
                    context.getArgument("player", PlayerSelectorArgumentResolver.class)
                        .resolve(context.getSource())
                        .getFirst()
                ))
            )
            .executes(context -> clearHome(context.getSource().getSender(), null));
    }

}
