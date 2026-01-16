package me.whizvox.wessentials.command.customtext;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomTextCommand extends ModuleCommand {

    private final Permission examplePermission;
    private final Permission listPermission;
    private final Permission editPermission;

    public CustomTextCommand() {
        super("customtext", "customtext", "customtext");
        examplePermission = new Permission("wessentials.customtext.example");
        listPermission = new Permission("wessentials.customtext.list");
        editPermission = new Permission("wessentials.customtext.edit");
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.literal("example")
                .requires(source -> source.getSender().hasPermission(examplePermission))
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    File file = new File(WEssentials.inst().getDataFolder(), "customtext.yml");
                    if (file.exists()) {
                        sender.sendMessage(WEssentials.translate("message.customtext.exampleExists"));
                    } else {
                        WEssentials.inst().saveResource("customtext.yml", false);
                        sender.sendMessage(WEssentials.translate("message.customtext.example", Map.of("file", file)));
                    }
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("list")
                .requires(source -> source.getSender().hasPermission(listPermission))
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    Collection<String> keys = WEssentials.inst().getCustomText().getKeys();
                    if (!keys.isEmpty()) {
                        List<String> sortedKeys = new ArrayList<>(keys);
                        sortedKeys.sort(String::compareTo);
                        Component listComp = Component.text("");
                        boolean first = true;
                        for (String key : sortedKeys) {
                            if (!first) {
                                listComp = listComp.append(Component.text(", "));
                            } else {
                                first = false;
                            }
                            listComp = listComp.append(Component.text("/" + key, NamedTextColor.YELLOW));
                        }
                        sender.sendMessage(WEssentials.translate("message.customtext.list", Map.of("commands", listComp)));
                    } else {
                        sender.sendMessage(WEssentials.translate("message.customtext.listNone"));
                    }
                    return Command.SINGLE_SUCCESS;
                })
            )
            /*.then(Commands.literal("edit")
                .requires(source -> source.getSender().hasPermission(editPermission))
                .then(Commands.argument("key", StringArgumentType.word())
                    .suggests((context, sBuilder) -> {
                        SuggestionsUtil.customTextKeys(sBuilder);
                        return sBuilder.buildFuture();
                    })
                    .then(Commands.argument("line", IntegerArgumentType.integer(1, 25))
                        .then(Commands.argument("text", StringArgumentType.greedyString())
                            .executes(context -> {
                                CommandSender sender = context.getSource().getSender();
                                String key = StringArgumentType.getString(context, "key");
                                int line = IntegerArgumentType.getInteger(context, "line");
                                String text = StringArgumentType.getString(context, "text");
                                List<String> lines = WEssentials.inst().getCustomText().getRawText(key);
                                if (lines == null) {
                                    throw WECommandExceptions.UNKNOWN_CUSTOM_TEXT_KEY.create(key);
                                }
                                List<String> newLines;
                                if (line <= lines.size()) {
                                    newLines = new ArrayList<>(lines);
                                    newLines.set(line - 1, text);
                                } else {
                                    newLines = new ArrayList<>(line);
                                    newLines.addAll(lines);
                                    for (int i = lines.size(); i < line - 1; i++) {
                                        newLines.add("");
                                    }
                                    newLines.add(text);
                                }
                                WEssentials.inst().getCustomText().setText(key, newLines);
                                sender.sendMessage(WEssentials.translate("message.customtext.update"));
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )
            )*/;
    }

}
