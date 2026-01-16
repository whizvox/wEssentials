package me.whizvox.wessentials.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.whizvox.wessentials.WEssentials;
import me.whizvox.wessentials.core.ModuleCommand;
import org.bukkit.permissions.Permission;

import java.util.Map;

public class WEssentialsCommand extends ModuleCommand {

    private final Permission
            reloadPermission,
            versionPermission;

    public WEssentialsCommand() {
        super(null, "wessentials", "command");
        reloadPermission = new Permission("wessentials.command.reload");
        versionPermission = new Permission("wessentials.command.version");
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
            .then(Commands.literal("reload")
                .requires(source -> source.getSender().hasPermission(reloadPermission))
                .executes(context -> {
                    WEssentials.inst().reload();
                    context.getSource().getSender().sendMessage(WEssentials.translate(
                        "message.reload", Map.of("plugin_name", WEssentials.translate("message.pluginName"))
                    ));
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then(Commands.literal("version")
                .requires(source -> source.getSender().hasPermission(versionPermission))
                .executes(context -> {
                    context.getSource().getSender().sendMessage(WEssentials.translate(
                        "message.version",
                        Map.of(
                            "plugin_name", WEssentials.translate("message.pluginName"),
                            "version", WEssentials.inst().getPluginMeta().getVersion()
                        )
                    ));
                    return Command.SINGLE_SUCCESS;
                })
            );
    }

}
