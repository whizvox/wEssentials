package me.whizvox.wessentials.command.kit;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.KitArgumentType;
import me.whizvox.wessentials.core.ModuleCommand;
import me.whizvox.wessentials.module.kit.Kit;
import org.bukkit.command.CommandSender;

public class DeleteKitCommand extends ModuleCommand {

    public DeleteKitCommand() {
        super("kit", "delkit", "kit.delete");
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.argument("kit", KitArgumentType.kit())
                .executes(context -> {
                    Kit kit = KitArgumentType.getKit(context, "kit");
                    CommandSender sender = context.getSource().getSender();
                    if (WEssentials.inst().getKits().delete(kit.name())) {
                        WEssentials.inst().getKits().save();
                        sender.sendMessage(WEssentials.translate("message.kit.delete.success"));
                    } else {
                        sender.sendMessage(WEssentials.translate("message.kit.delete.fail"));
                    }
                    return Command.SINGLE_SUCCESS;
                })
            );
    }

}
