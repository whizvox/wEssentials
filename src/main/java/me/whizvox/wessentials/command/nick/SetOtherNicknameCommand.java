package me.whizvox.wessentials.command.nick;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class SetOtherNicknameCommand extends ModuleCommand {

    public SetOtherNicknameCommand() {
        super("nickname", "setnick", "nick.other");
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.argument("player", ArgumentTypes.player())
                .then(Commands.argument("nickname", StringArgumentType.greedyString())
                    .executes(context -> {
                        CommandSender sender = context.getSource().getSender();
                        Player receiver = context.getArgument("player", PlayerSelectorArgumentResolver.class)
                            .resolve(context.getSource())
                            .getFirst();
                        String nickname = StringArgumentType.getString(context, "nickname");
                        Component nickComp = WEssentials.inst().getNicknames().setNickname(sender, receiver, nickname);
                        WEssentials.inst().saveNicknames();
                        if (sender != receiver) {
                            sender.sendMessage(WEssentials.translate("message.nick.setOther", Map.of("player", receiver.getName(), "nick", nickComp)));
                        }
                        receiver.sendMessage(WEssentials.translate("message.nick.set", Map.of("nick", nickComp)));
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );
    }

}
