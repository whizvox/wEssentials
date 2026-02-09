package me.whizvox.wessentials.command.teleport;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class TeleportCancelCommand extends ModuleCommand {

    public TeleportCancelCommand() {
        super("teleportrequest", "tpcancel", (Permission) null);
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
                WEssentials.inst().getTeleports().cancel(player);
                return Command.SINGLE_SUCCESS;
            });
    }

}
