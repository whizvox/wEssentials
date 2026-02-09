package me.whizvox.wessentials.core;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

public abstract class ModuleCommand {

    private final @Nullable String module;
    private final String command;
    private final @Nullable Permission permission;

    public ModuleCommand(@Nullable String module, String command, @Nullable Permission permission) {
        this.module = module;
        this.command = command;
        this.permission = permission;
    }

    public ModuleCommand(String module, String command, @Nullable String permissionBase) {
        this(module, command, permissionBase == null ? null : new Permission("wessentials." + permissionBase));
    }

    public ModuleCommand(String module, String command) {
        this(module, command, command);
    }

    public @Nullable String getModule() {
        return module;
    }

    public String getCommand() {
        return command;
    }

    public boolean hasPermission(CommandSourceStack source) {
        if (permission == null) {
            return true;
        }
        return source.getSender().hasPermission(permission);
    }

    public void register(Commands registrar) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(command)
                .requires(this::hasPermission);
        register(builder);
        registrar.register(builder.build());
    }

    protected abstract void register(LiteralArgumentBuilder<CommandSourceStack> builder);

}
