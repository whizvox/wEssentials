package me.whizvox.wessentials.module.chat;

import org.bukkit.entity.Player;

public interface PrefixSuffixProvider {

    String getPrefix(Player player);

    String getSuffix(Player player);

    PrefixSuffixProvider EMPTY = new EmptyPrefixSuffixProvider();

}
