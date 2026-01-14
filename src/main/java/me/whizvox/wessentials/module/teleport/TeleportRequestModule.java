package me.whizvox.wessentials.module;

import org.bukkit.entity.Player;

import java.util.List;

public class TeleportRequestModule {

    private final List<TeleportRequest> requests;

    public TeleportRequestModule() {
    }

    public record TeleportRequest(Player teleporter, Player destination) {
    }

}
