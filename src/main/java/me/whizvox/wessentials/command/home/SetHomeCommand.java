package me.whizvox.wessentials.command.home;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SetHomeCommand extends ModuleCommand {

    private final Permission otherPermission;

    public SetHomeCommand() {
        super("home", "sethome", "home.set");
        otherPermission = new Permission("wessentials.home.set.other");
    }

    private int setHome(Player sender, @Nullable Player receiver) {
        if (receiver == null) {
            receiver = sender;
        }
        WEssentials.inst().getHomes().setHome(receiver, sender.getLocation(), false);
        WEssentials.inst().getHomes().save();
        receiver.sendMessage(WEssentials.translate("message.home.set"));
        if (sender != receiver) {
            sender.sendMessage(WEssentials.translate("message.home.setOther", Map.of("player", receiver.displayName())));
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
                .executes(context -> setHome(
                    (Player) context.getSource().getSender(),
                    context.getArgument("player", PlayerSelectorArgumentResolver.class)
                        .resolve(context.getSource())
                        .getFirst()
                ))
            )
            .executes(context -> setHome((Player) context.getSource().getSender(), null));
    }

}
