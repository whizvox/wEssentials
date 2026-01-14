package me.whizvox.wessentials.module.chat;

import org.bukkit.entity.Player;

public class EmptyPrefixSuffixProvider implements PrefixSuffixProvider {

    @Override
    public String getPrefix(Player player) {
        return "";
    }

    @Override
    public String getSuffix(Player player) {
        return "";
    }

}
