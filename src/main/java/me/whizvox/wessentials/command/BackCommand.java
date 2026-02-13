package me.whizvox.wessentials.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import me.whizvox.wessentials.module.back.BackLocation;
import me.whizvox.wessentials.util.LocationUtil;
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

    private int teleportBack(CommandContext<CommandSourceStack> context, boolean unsafe) {
        Player player = (Player) context.getSource().getSender();
        BackLocation loc = WEssentials.inst().getBack().getLocation(player);
        if (loc != null) {
            Location tpLoc;
            if (LocationUtil.isSafe(loc.location()) || unsafe) {
                // the location is safe or the player is allowed to teleport to unsafe locations
                tpLoc = loc.location();
            } else if (loc.safeLocation() != null) {
                // there is a safe location found, but we're not sure if it's safe anymore
                if (LocationUtil.isSafe(loc.safeLocation())) {
                    // safe location is indeed safe
                    tpLoc = loc.safeLocation();
                } else {
                    // safe location is no longer safe, try and find a new one
                    Location safeLocation = WEssentials.inst().getBack().findSafeLocation(loc.location());
                    if (safeLocation == null) {
                        // update the back location
                        BackLocation newLoc = new BackLocation(loc.location(), false, null);
                        WEssentials.inst().getBack().setLocation(player, newLoc);
                        tpLoc = null;
                    } else {
                        tpLoc = safeLocation;
                    }
                }
            } else {
                // location is not safe and no safe location was originally found
                if (WEssentials.inst().getBack().isSafeSearchEnabled()) {
                    // try and find a new safe location
                    tpLoc = WEssentials.inst().getBack().findSafeLocation(loc.location());
                } else {
                    tpLoc = null;
                }
            }
            if (tpLoc != null) {
                WEssentials.inst().getBack().markBacktracking(player);
                player.teleportAsync(tpLoc, PlayerTeleportEvent.TeleportCause.COMMAND)
                    .thenAccept(success -> {
                        if (success) {
                            WEssentials.inst().getBack().clearLocation(player);
                            WEssentials.inst().getBack().unmarkBacktracking(player);
                            WEssentials.inst().getBack().save();
                            player.sendActionBar(WEssentials.translate("message.back.success"));
                        } else {
                            player.sendMessage(WEssentials.translate("message.back.fail"));
                        }
                    });
            } else {
                if (player.hasPermission(WEssentials.inst().getBack().unsafePermission)) {
                    player.sendMessage(WEssentials.translate("message.back.unsafePermission"));
                } else {
                    player.sendMessage(WEssentials.translate("message.back.unsafe"));
                }
            }
        } else {
            player.sendMessage(WEssentials.translate("message.back.noLocation"));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.literal("unsafe")
                .requires(source -> source.getSender().hasPermission(WEssentials.inst().getBack().unsafePermission))
                .executes(context -> teleportBack(context, true))
            )
            .executes(context -> teleportBack(context, false));
    }

}
