package me.whizvox.wessentials.module.teleport;

import me.whizvox.wessentials.WEssentials;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeleportRequestModule implements Listener {

    private final List<TeleportRequest> requests;

    public TeleportRequestModule() {
        requests = new ArrayList<>();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cancelAll(event.getPlayer());
    }

    public void clear() {
        requests.clear();
    }

    public boolean hasRequest(Player player) {
        return requests.stream().anyMatch(request -> request.sender() == player);
    }

    public void submit(TeleportRequest request) {
        TeleportRequest found = null;
        for (TeleportRequest other : requests) {
            if (other.sender() == request.sender()) {
                found = other;
                break;
            }
        }
        if (found == null) {
            requests.add(request);
            Component remaining = WEssentials.translate("message.teleport.request.receiver.remaining");
            if (request.toSender()) {
                request.receiver().sendMessage(WEssentials.translate("message.teleport.request.receiver.toSender", Map.of("player", request.sender().displayName(), "remaining", remaining)));
            } else {
                request.receiver().sendMessage(WEssentials.translate("message.teleport.request.receiver.toReceiver", Map.of("player", request.sender().displayName(), "remaining", remaining)));
            }
            request.sender().sendMessage(WEssentials.translate("message.teleport.request.sender", Map.of("player", request.receiver().displayName())));
        } else {
            request.sender().sendMessage(WEssentials.translate("message.teleport.hasRequest", Map.of("player", request.receiver().displayName())));
        }
    }

    public void removeInvalid() {
        LocalDateTime now = LocalDateTime.now();
        requests.removeIf(request -> {
            if (now.isAfter(request.timeout())) {
                request.receiver().sendMessage(WEssentials.translate("message.teleport.expired", Map.of("player", request.sender().displayName())));
                request.sender().sendMessage(WEssentials.translate("message.teleport.expired", Map.of("player", request.receiver().displayName())));
                return true;
            }
            if (!request.sender().isConnected()) {
                if (request.receiver().isConnected()) {
                    request.receiver().sendMessage(WEssentials.translate("message.teleport.cancel.offline", Map.of("player", request.sender().displayName())));
                }
                return true;
            } else if (!request.receiver().isConnected()) {
                request.sender().sendMessage(WEssentials.translate("message.teleport.cancel.offline", Map.of("player", request.receiver().displayName())));
                return true;
            }
            return false;
        });
    }

    public void accept(Player receiver, @Nullable Player sender) {
        TeleportRequest found = null;
        for (TeleportRequest request : requests) {
            if (request.receiver() == receiver && (sender == null || request.sender() == sender)) {
                found = request;
                break;
            }
        }
        if (found != null) {
            boolean doTeleport = true;
            if (LocalDateTime.now().isBefore(found.timeout())) {
                if (!found.sender().isConnected()) {
                    receiver.sendMessage(WEssentials.translate("message.teleport.offline", Map.of("player", found.sender().displayName())));
                    doTeleport = false;
                }
            } else {
                receiver.sendMessage(WEssentials.translate("message.teleport.expired", Map.of("player", found.sender().displayName())));
                found.sender().sendMessage(WEssentials.translate("message.teleport.expired", Map.of("player", receiver.displayName())));
                doTeleport = false;
            }
            if (doTeleport) {
                if (found.toSender()) {
                    if (receiver.hasPermission(WEssentials.inst().getBack().tpaPermission)) {
                        WEssentials.inst().getBack().setLocation(receiver, receiver.getLocation());
                        WEssentials.inst().getBack().save();
                    }
                    receiver.teleport(found.sender(), PlayerTeleportEvent.TeleportCause.COMMAND);
                    receiver.sendMessage(WEssentials.translate("message.teleport.success.to", Map.of("player", found.sender().displayName())));
                    found.sender().sendMessage(WEssentials.translate("message.teleport.success.from", Map.of("player", receiver.displayName())));
                } else {
                    if (found.sender().hasPermission(WEssentials.inst().getBack().tpaPermission)) {
                        WEssentials.inst().getBack().setLocation(found.sender(), found.sender().getLocation());
                        WEssentials.inst().getBack().save();
                    }
                    found.sender().teleport(receiver, PlayerTeleportEvent.TeleportCause.COMMAND);
                    receiver.sendMessage(WEssentials.translate("message.teleport.success.from", Map.of("player", found.sender().displayName())));
                    found.sender().sendMessage(WEssentials.translate("message.teleport.success.to", Map.of("player", receiver.displayName())));
                }
            }
            requests.remove(found);
        } else {
            if (sender == null) {
                receiver.sendMessage(WEssentials.translate("message.teleport.noRequest"));
            } else {
                receiver.sendMessage(WEssentials.translate("message.teleport.noRequestPlayer", Map.of("player", sender.displayName())));
            }
        }
    }

    public void deny(Player receiver, @Nullable Player sender) {
        TeleportRequest found = null;
        for (TeleportRequest request : requests) {
            if (request.receiver() == receiver && (sender == null || sender == request.sender())) {
                found = request;
                break;
            }
        }
        if (found != null) {
            receiver.sendMessage(WEssentials.translate("message.teleport.deny.receiver", Map.of("player", found.sender().displayName())));
            found.sender().sendMessage(WEssentials.translate("message.teleport.deny.sender", Map.of("player", receiver.displayName())));
            requests.remove(found);
        } else {
            if (sender == null) {
                receiver.sendMessage(WEssentials.translate("message.teleport.noRequest"));
            } else {
                receiver.sendMessage(WEssentials.translate("message.teleport.noRequestPlayer", Map.of("player", sender.displayName())));
            }
        }
    }

    public void cancel(Player sender) {
        TeleportRequest found = null;
        for (TeleportRequest request : requests) {
            if (request.sender() == sender) {
                found = request;
                break;
            }
        }
        if (found != null) {
            found.receiver().sendMessage(WEssentials.translate("message.teleport.cancel.receiver", Map.of("player", sender.displayName())));
            sender.sendMessage(WEssentials.translate("message.teleport.cancel.sender", Map.of("player", found.receiver().displayName())));
            requests.remove(found);
        } else {
            sender.sendMessage(WEssentials.translate("message.teleport.noRequest"));
        }
    }

    public void cancelAll(Player player) {
        requests.removeIf(request -> {
            if (request.sender() == player) {
                request.receiver().sendMessage(WEssentials.translate("message.teleport.cancel.offline", Map.of("player", player.displayName())));
                return true;
            } else if (request.receiver() == player) {
                request.sender().sendMessage(WEssentials.translate("message.teleport.cancel.offline", Map.of("player", player.displayName())));
                return true;
            }
            return false;
        });
    }

}
