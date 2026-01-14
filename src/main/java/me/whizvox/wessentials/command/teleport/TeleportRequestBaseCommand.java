package me.whizvox.wessentials.command.tp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import me.whizvox.wessentials.module.teleport.TeleportRequest;
import me.whizvox.wessentials.util.SuggestionsUtil;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.Map;

public class TeleportRequestBaseCommand extends ModuleCommand {

    private final boolean toSender;
    private final Permission receivePermission;
    private final Permission bypassReceivePermission;

    public TeleportRequestBaseCommand(String command, String permissionBase, boolean toSender) {
        super("teleport", command, permissionBase);
        this.toSender = toSender;
        receivePermission = new Permission("wessentials.teleport.receive");
        bypassReceivePermission = new Permission("wessentials.teleport.bypassReceive");
    }

    @Override
    public boolean hasPermission(CommandSourceStack source) {
        return super.hasPermission(source) && source.getSender() instanceof Player;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.argument("player", ArgumentTypes.player())
                .suggests(SuggestionsUtil.ONLINE_PLAYERS)
                .executes(context -> {
                    Player sender = (Player) context.getSource().getSender();
                    var resolver = context.getArgument("player", PlayerSelectorArgumentResolver.class);
                    Player receiver = resolver.resolve(context.getSource()).getFirst();
                    if (sender.hasPermission(bypassReceivePermission) || receiver.hasPermission(receivePermission)) {
                        if (!sender.getUniqueId().equals(receiver.getUniqueId())) {
                            WEssentials.inst().getTeleports().submit(TeleportRequest.create(sender, receiver, toSender));
                        } else {
                            sender.sendMessage(WEssentials.translate("message.teleport.notSelf"));
                        }
                    } else {
                        sender.sendMessage(WEssentials.translate("message.teleport.cannotSend", Map.of("player", receiver.displayName())));
                    }
                    return Command.SINGLE_SUCCESS;
                })
            );
    }

}
