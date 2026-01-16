package me.whizvox.wessentials.command.warp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import me.whizvox.wessentials.core.WarpArgumentType;
import me.whizvox.wessentials.module.warp.WarpLocation;

import java.util.Map;

public class DeleteWarpCommand extends ModuleCommand {

    public DeleteWarpCommand() {
        super("warp", "delwarp", "warp.delete");
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.then(Commands.argument("warp", WarpArgumentType.warp())
            .executes(context -> {
                WarpLocation warp = WarpArgumentType.getWarp(context, "warp");
                WEssentials.inst().getWarps().delete(warp.name());
                WEssentials.inst().getWarps().save();
                context.getSource().getSender().sendMessage(WEssentials.translate("message.warp.delete", Map.of("warp", warp.name())));
                return Command.SINGLE_SUCCESS;
            })
        );
    }

}
