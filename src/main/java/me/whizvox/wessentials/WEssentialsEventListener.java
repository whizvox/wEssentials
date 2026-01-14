package me.whizvox.wessentials;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WEssentialsEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Component nick = WEssentials.inst().getNicknames().getNickname(event.getPlayer());
        if (nick != null) {
            event.getPlayer().displayName(nick);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        WEssentials.inst().getTeleports().cancelAll(event.getPlayer());
    }

}
