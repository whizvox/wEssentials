package me.whizvox.wessentials.command.customtext;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import me.whizvox.wessentials.module.customtext.CustomTextModule;
import net.kyori.adventure.text.Component;

public class ShowCustomTextCommand extends ModuleCommand {

    private final CustomTextModule customText;
    private final String key;

    public ShowCustomTextCommand(String key, CustomTextModule customText) {
        super("customtext", key, customText.getPermission(key));
        this.key = key;
        this.customText = customText;
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .executes(context -> {
                Component message = customText.getMessage(key);
                if (message != null) {
                    context.getSource().getSender().sendMessage(message);
                } else {
                    context.getSource().getSender().sendMessage(WEssentials.translate("message.customtext.none"));
                }
                return Command.SINGLE_SUCCESS;
            });
    }

}
