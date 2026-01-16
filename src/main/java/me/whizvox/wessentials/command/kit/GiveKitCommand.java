package me.whizvox.wessentials.command.kit;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.KitArgumentType;
import me.whizvox.wessentials.core.ModuleCommand;
import me.whizvox.wessentials.exception.WECommandExceptions;
import me.whizvox.wessentials.module.kit.Kit;
import me.whizvox.wessentials.module.kit.KitModule;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class GiveKitCommand extends ModuleCommand {

    private final Permission
        otherPermission,
        bypassCooldownPermission;

    public GiveKitCommand() {
        super("kit", "kit", "kit.give");
        otherPermission = new Permission("wessentials.kit.give.other");
        bypassCooldownPermission = new Permission("wessentials.kit.give.bypassCooldown");
    }

    private int giveKit(CommandSender sender, @Nullable Player receiver, Kit kit) throws CommandSyntaxException {
        if (receiver == null) {
            receiver = (Player) sender;
        }
        if (!sender.hasPermission(kit.getPermission())) {
            throw WECommandExceptions.UNKNOWN_KIT.create(kit.name());
        }
        LocalDateTime cooldownExpiration = WEssentials.inst().getKits().getCooldownExpiration(receiver, kit.name());
        boolean senderBypassCooldown = sender.hasPermission(bypassCooldownPermission);
        boolean receiverBypassCooldown = sender == receiver ? senderBypassCooldown : receiver.hasPermission(bypassCooldownPermission);
        if (cooldownExpiration == null || senderBypassCooldown) {
            WEssentials.inst().getKits().give(receiver, kit);
            Component displayName = WEssentials.inst().getKits().getDisplayName(kit.name());
            assert displayName != null;
            receiver.sendMessage(WEssentials.translate("message.kit.give", Map.of("kit", displayName)));
            if (sender != receiver) {
                sender.sendMessage(WEssentials.translate("message.kit.giveOther", Map.of("player", receiver.displayName(), "kit", displayName)));
            }
            if (receiverBypassCooldown) {
               WEssentials.inst().getKits().clearCooldown(receiver, kit.name());
            } else {
                WEssentials.inst().getKits().applyCooldown(receiver, kit);
            }
            WEssentials.inst().getKits().save();
        } else {
            Duration duration = Duration.between(LocalDateTime.now(), cooldownExpiration);
            if (sender == receiver) {
                sender.sendMessage(WEssentials.translate("message.kit.cooldown", Map.of("cooldown", duration.getSeconds())));
            } else {
                sender.sendMessage(WEssentials.translate("message.kit.cooldownOther", Map.of("cooldown", duration.getSeconds(), "player", receiver.displayName())));
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.argument("kit", KitArgumentType.kit())
                .then(Commands.argument("player", ArgumentTypes.player())
                    .requires(source -> source.getSender().hasPermission(otherPermission))
                    .executes(context -> giveKit(
                        context.getSource().getSender(),
                        context.getArgument("player", PlayerSelectorArgumentResolver.class)
                            .resolve(context.getSource())
                            .getFirst(),
                        KitArgumentType.getKit(context, "kit")
                    ))
                )
                .requires(source -> source.getSender() instanceof Player)
                .executes(context -> giveKit(
                    context.getSource().getSender(),
                    null,
                    KitArgumentType.getKit(context, "kit")
                ))
            );
    }

}
