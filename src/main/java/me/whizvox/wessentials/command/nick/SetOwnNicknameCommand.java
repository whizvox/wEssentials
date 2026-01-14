package me.whizvox.wessentials.command.nick;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Map;

public class SetOwnNicknameCommand extends ModuleCommand {

    public SetOwnNicknameCommand() {
        super("nickname", "nick");
    }

    @Override
    public boolean hasPermission(CommandSourceStack source) {
        return super.hasPermission(source) && source.getSender() instanceof Player;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.argument("nickname", StringArgumentType.greedyString())
                .executes(context -> {
                    Player sender = (Player) context.getSource().getSender();
                    String nickname = StringArgumentType.getString(context, "nickname");
                    Component nickComp = WEssentials.inst().getNicknames().setNickname(sender, sender, nickname);
                    WEssentials.inst().saveNicknames();
                    sender.sendMessage(WEssentials.translate("message.nick.set", Map.of("nick", nickComp)));
                    return Command.SINGLE_SUCCESS;
                })
            );
    }

}
