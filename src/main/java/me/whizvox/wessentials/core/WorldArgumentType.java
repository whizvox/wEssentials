package me.whizvox.wessentials.core;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.World;

public class WorldCommandArgumentType implements CustomArgumentType.Converted<World, String> {
    @Override
    public World convert(String nativeType) throws CommandSyntaxException {
        return null;
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return null;
    }
}
