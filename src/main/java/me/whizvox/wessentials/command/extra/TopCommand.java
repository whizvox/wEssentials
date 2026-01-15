package me.whizvox.wessentials.command.extra;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TopCommand extends ModuleCommand {

    public TopCommand() {
        super("extra", "top");
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
                World world = player.getWorld();
                int x = player.getLocation().getBlockX();
                int z = player.getLocation().getBlockZ();
                boolean teleported = false;
                for (int y = world.getMaxHeight(); y >= world.getMinHeight(); y--) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.isCollidable()) {
                        Location loc = player.getLocation().clone();
                        loc.setX(x + 0.5);
                        loc.setY(y + 1);
                        loc.setZ(z + 0.5);
                        player.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
                        teleported = true;
                        player.showTitle(Title.title(Component.empty(), WEssentials.translate("message.top.success"), 5, 15, 5));
                        break;
                    }
                }
                if (!teleported) {
                    player.sendMessage(WEssentials.translate("message.top.fail"));
                }
                return Command.SINGLE_SUCCESS;
            });
    }

}
