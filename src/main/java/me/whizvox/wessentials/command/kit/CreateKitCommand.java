package me.whizvox.wessentials.command.kit;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import me.whizvox.wessentials.exception.WECommandExceptions;
import me.whizvox.wessentials.module.kit.Kit;
import me.whizvox.wessentials.module.kit.SlottedItem;
import me.whizvox.wessentials.util.StringUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class CreateKitCommand extends ModuleCommand {

    public CreateKitCommand() {
        super("kit", "createkit", "kit.create");
    }

    @Override
    public boolean hasPermission(CommandSourceStack source) {
        return super.hasPermission(source) && source.getSender() instanceof Player;
    }

    private int createKit(Player player, String name) throws CommandSyntaxException {
        if (!StringUtil.isValidGenericName(name)) {
            throw WECommandExceptions.INVALID_KIT_NAME.create(name);
        }
        List<SlottedItem> inventory = SlottedItem.getItems(player);
        Kit kit = WEssentials.inst().getKits().create(name, inventory);
        if (kit != null) {
            WEssentials.inst().getKits().save();
            Component displayName = WEssentials.inst().getKits().getDisplayName(name);
            player.sendMessage(WEssentials.translate("message.kit.create", Map.of("kit", displayName)));
        } else {
            player.sendMessage(WEssentials.translate("message.kit.conflict", Map.of("kit", name)));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.argument("name", StringArgumentType.word())
                .executes(context -> createKit(
                    (Player) context.getSource().getSender(),
                    StringArgumentType.getString(context, "name")
                ))
            );
    }

}
