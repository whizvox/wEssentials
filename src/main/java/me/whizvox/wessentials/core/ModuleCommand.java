package me.whizvox.wessentials.core;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.permissions.Permission;

public abstract class CommandModule {

    private final String command;
    private final Permission permission;

    public CommandModule(String command, Permission permission) {
        this.command = command;
        this.permission = permission;
    }

    public CommandModule(String command, String permissionBase) {
        this(command, new Permission("wessentials.command." + permissionBase));
    }

    public CommandModule(String command) {
        this(command, command);
    }

    public void register(Commands registrar) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(command)
            .requires(source -> source.getSender().hasPermission(permission));
        register(builder);
        registrar.register(builder.build());
    }

    protected abstract void register(LiteralArgumentBuilder<CommandSourceStack> builder);

}
