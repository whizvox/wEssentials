package me.whizvox.wessentials.module.chat;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;

import java.util.Objects;

public class LuckPermsPrefixSuffixProvider implements PrefixSuffixProvider {

    private final LuckPerms luckPerms;

    public LuckPermsPrefixSuffixProvider() {
        this.luckPerms = LuckPermsProvider.get();
    }

    @Override
    public String getPrefix(Player player) {
        String prefix = luckPerms.getPlayerAdapter(Player.class).getMetaData(player).getPrefix();
        return Objects.requireNonNullElse(prefix, "");
    }

    @Override
    public String getSuffix(Player player) {
        String suffix = luckPerms.getPlayerAdapter(Player.class).getMetaData(player).getSuffix();
        return Objects.requireNonNullElse(suffix, "");
    }

}
