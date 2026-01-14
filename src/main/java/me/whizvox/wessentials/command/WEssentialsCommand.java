package me.whizvox.wessentials.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.whizvox.wessentials.core.ModuleCommand;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

public class ReloadCommand extends ModuleCommand {

    public ReloadCommand(@Nullable String module, String command, @Nullable Permission permission) {
        super(module, command, permission);
    }

    @Override
    protected void register(LiteralArgumentBuilder<CommandSourceStack> builder) {

    }
}
