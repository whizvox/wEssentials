package me.whizvox.wessentials;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class WEssentialsEventListener implements Listener {

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        WEssentials.inst().getTeleports().cancelAll(event.getPlayer());
    }

}
