package me.whizvox.wessentials.module.kit;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.List;

public interface KitSelectorResolver {

    List<Kit> resolve() throws CommandSyntaxException;

}
