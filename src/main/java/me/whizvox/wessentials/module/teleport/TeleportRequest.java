package me.whizvox.wessentials.module.teleport;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public record TeleportRequest(Player sender, Player receiver, boolean toSender, LocalDateTime timeout) {

    public static TeleportRequest create(Player sender, Player receiver, boolean toSender, int durationSeconds) {
        return new TeleportRequest(sender, receiver, toSender, LocalDateTime.now().plusSeconds(durationSeconds));
    }

    public static TeleportRequest create(Player sender, Player receiver, boolean toSender) {
        return create(sender, receiver, toSender, 60);
    }

}
