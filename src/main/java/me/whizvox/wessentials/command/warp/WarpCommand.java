package me.whizvox.wessentials.command.warp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import me.whizvox.wessentials.core.WarpArgumentType;
import me.whizvox.wessentials.exception.WECommandExceptions;
import me.whizvox.wessentials.module.warp.WarpLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Map;

public class WarpCommand extends ModuleCommand {

    public WarpCommand() {
        super("warp", "warp", "warp.teleport");
    }

    @Override
    public boolean hasPermission(CommandSourceStack source) {
        return super.hasPermission(source) && source.getSender() instanceof Player;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.then(Commands.argument("warp", WarpArgumentType.warp())
            .executes(context -> {
                Player player = (Player) context.getSource().getSender();
                WarpLocation warp = WarpArgumentType.getWarp(context, "warp");
                if (player.hasPermission("wessentials.warp.access." + warp.name())) {
                    player.teleport(warp.location(), PlayerTeleportEvent.TeleportCause.COMMAND);
                    player.sendMessage(WEssentials.translate("message.warp.teleport", Map.of("warp", warp.name())));
                } else {
                    throw WECommandExceptions.WARP_NOT_FOUND.create(warp.name());
                }
                return Command.SINGLE_SUCCESS;
            })
        );
    }

}
