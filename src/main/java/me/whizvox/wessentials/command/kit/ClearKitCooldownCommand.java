package me.whizvox.wessentials.command.kit;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.KitSelectorArgumentType;
import me.whizvox.wessentials.core.ModuleCommand;
import me.whizvox.wessentials.module.kit.Kit;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ClearKitCooldownCommand extends ModuleCommand {

    public ClearKitCooldownCommand() {
        super("kit", "clearcooldown", "kit.clearCooldown");
    }

    private int clearCooldown(CommandSender sender, @Nullable Player receiver, List<Kit> kits) {
        if (receiver == null) {
            receiver = (Player) sender;
        }
        boolean anyCleared = false;
        for (Kit kit : kits) {
            if (WEssentials.inst().getKits().clearCooldown(receiver, kit.name())) {
                anyCleared = true;
            }
        }
        if (anyCleared) {
            if (kits.size() == 1) {
                Component displayName = WEssentials.inst().getKits().getDisplayName(kits.getFirst().name());
                if (sender != receiver) {
                    sender.sendMessage(WEssentials.translate("message.kit.clearCooldown.other", Map.of("player", receiver.displayName(), "kit", displayName)));
                }
                receiver.sendMessage(WEssentials.translate("message.kit.clearCooldown.self", Map.of("kit", displayName)));
            } else {
                if (sender != receiver) {
                    sender.sendMessage(WEssentials.translate("message.kit.clearCooldown.all.sender", Map.of("player", receiver.displayName())));
                }
                receiver.sendMessage(WEssentials.translate("message.kit.clearCooldown.all.receiver"));
            }
        } else {
            sender.sendMessage(WEssentials.translate("message.kit.clearCooldown.none"));
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean hasPermission(CommandSourceStack source) {
        // only applies to the command with no arguments
        return super.hasPermission(source) && source.getSender() instanceof Player;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.argument("kit", KitSelectorArgumentType.kits())
                .then(Commands.argument("player", ArgumentTypes.player())
                    .executes(context -> clearCooldown(
                        context.getSource().getSender(),
                        context.getArgument("player", PlayerSelectorArgumentResolver.class)
                            .resolve(context.getSource())
                            .getFirst(),
                        KitSelectorArgumentType.getKits(context, "kit").resolve()
                    ))
                )
                .executes(context -> clearCooldown(
                    context.getSource().getSender(),
                    null,
                    KitSelectorArgumentType.getKits(context, "kit").resolve()
                ))
            )
            .executes(context -> clearCooldown(
                context.getSource().getSender(),
                null,
                WEssentials.inst().getKits().getAll().stream().toList()
            ));
    }

}
