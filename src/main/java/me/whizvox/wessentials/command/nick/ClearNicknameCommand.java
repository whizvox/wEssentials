package me.whizvox.wessentials.command.nick;

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

import java.util.Map;

public class ClearNicknameCommand extends ModuleCommand {

    private final Permission otherPermission;

    public ClearNicknameCommand() {
        super("nickname", "clearnick", "nick");
        otherPermission = new Permission("wessentials.nick.other");
    }

    private int clearNickname(CommandSender sender, Player receiver) {
        if (WEssentials.inst().getNicknames().clearNickname(receiver)) {
            WEssentials.inst().getNicknames().save();
            if (sender != receiver) {
                sender.sendMessage(WEssentials.translate("message.nick.clearOther", Map.of("player", receiver.getName())));
            }
            receiver.sendMessage(WEssentials.translate("message.nick.clear"));
        } else {
            if (sender == receiver) {
                sender.sendMessage(WEssentials.translate("message.nick.notSet"));
            } else {
                sender.sendMessage(WEssentials.translate("message.nick.notSetOther", Map.of("player", receiver.getName())));
            }
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
                .executes(context -> clearNickname(
                    context.getSource().getSender(),
                    context.getArgument("player", PlayerSelectorArgumentResolver.class)
                        .resolve(context.getSource())
                        .getFirst()
                ))
            )
            .executes(context -> clearNickname(
                context.getSource().getSender(),
                (Player) context.getSource().getSender()
            ));
    }

}
