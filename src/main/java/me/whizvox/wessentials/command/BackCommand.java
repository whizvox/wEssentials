package me.whizvox.wessentials.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BackCommand extends ModuleCommand {

    public BackCommand() {
        super("back", "back");
    }

    @Override
    public boolean hasPermission(CommandSourceStack source) {
        return super.hasPermission(source) && source.getSender() instanceof Player;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .executes(context -> {
                Player player = (Player) context.getSource().getSender();
                Location loc = WEssentials.inst().getBack().getLocation(player);
                if (loc != null) {
                    WEssentials.inst().getBack().markBacktracking(player);
                    player.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.COMMAND)
                        .thenAccept(success -> {
                            if (success) {
                                WEssentials.inst().getBack().clearLocation(player);
                                WEssentials.inst().getBack().unmarkBacktracking(player);
                                WEssentials.inst().getBack().save();
                            } else {
                                player.sendMessage(WEssentials.translate("message.back.fail"));
                            }
                        });
                } else {
                    player.sendMessage(WEssentials.translate("message.back.noLocation"));
                }
                return Command.SINGLE_SUCCESS;
            });
    }

}
