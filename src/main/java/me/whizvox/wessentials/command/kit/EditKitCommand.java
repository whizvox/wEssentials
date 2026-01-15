package me.whizvox.wessentials.command.kit;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.KitArgumentType;
import me.whizvox.wessentials.core.ModuleCommand;
import me.whizvox.wessentials.exception.WECommandExceptions;
import me.whizvox.wessentials.module.kit.Kit;
import me.whizvox.wessentials.module.kit.SlottedItem;
import me.whizvox.wessentials.util.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class EditKitCommand extends ModuleCommand {

    public EditKitCommand() {
        super("kit", "editkit", "kit.edit");
    }

    private int updateKit(CommandSender sender, Kit oldKit, Kit newKit) {
        WEssentials.inst().getKits().update(oldKit, newKit);
        WEssentials.inst().getKits().save();
        sender.sendMessage(WEssentials.translate("message.kit.update"));
        return Command.SINGLE_SUCCESS;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.argument("kit", KitArgumentType.kit())
                .then(Commands.literal("name")
                    .then(Commands.argument("name", StringArgumentType.word())
                        .executes(context -> {
                            Kit oldKit = KitArgumentType.getKit(context, "kit");
                            String name = StringArgumentType.getString(context, "name");
                            if (StringUtil.isValidGenericName(name)) {
                                Kit newKit = oldKit.updateName(name);
                                updateKit(context.getSource().getSender(), oldKit, newKit);
                            } else {
                                throw WECommandExceptions.INVALID_KIT_NAME.create(name);
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
                .then(Commands.literal("displayName")
                    .then(Commands.argument("displayName", StringArgumentType.greedyString())
                        .executes(context -> {
                            Kit oldKit = KitArgumentType.getKit(context, "kit");
                            String displayName = StringArgumentType.getString(context, "displayName");
                            Kit newKit = oldKit.updateDisplayName(displayName);
                            return updateKit(context.getSource().getSender(), oldKit, newKit);
                        })
                    )
                )
                .then(Commands.literal("items")
                    .requires(source -> source.getSender() instanceof Player)
                    .executes(context -> {
                        Player player = (Player) context.getSource().getSender();
                        Kit oldKit = KitArgumentType.getKit(context, "kit");
                        List<SlottedItem> items = SlottedItem.getItems(player);
                        Kit newKit = oldKit.updateItems(items);
                        return updateKit(player, oldKit, newKit);
                    })
                )
                .then(Commands.literal("cooldown")
                    .then(Commands.argument("cooldown", IntegerArgumentType.integer(0))
                        .executes(context -> {
                            Kit oldKit = KitArgumentType.getKit(context, "kit");
                            int cooldown = IntegerArgumentType.getInteger(context, "cooldown");
                            Kit newKit = oldKit.updateCooldown(cooldown);
                            return updateKit(context.getSource().getSender(), oldKit, newKit);
                        })
                    )
                )
            );
    }

}
