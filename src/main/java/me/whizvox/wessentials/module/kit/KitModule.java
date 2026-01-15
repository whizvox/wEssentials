package me.whizvox.wessentials.module.kit;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.wessentials.module.SerializableModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;

@NotNullByDefault
public class KitModule extends SerializableModule {

    private final Map<String, Kit> kits;
    private final Map<String, Component> kitNamesCache;
    private final Map<CooldownKey, LocalDateTime> cooldowns;

    private boolean dropIfFull;

    public KitModule(Plugin plugin) {
        super(plugin, "kits.yml", false);
        kits = new Object2ObjectOpenHashMap<>();
        kitNamesCache = new Object2ObjectOpenHashMap<>();
        cooldowns = new Object2ObjectOpenHashMap<>();
        dropIfFull = true;
    }

    private void removeExpiredCooldowns() {
        LocalDateTime now = LocalDateTime.now();
        List<CooldownKey> toRemove = cooldowns.keySet().stream()
            // remove if expiration datetime has passed or the kit associated with the cooldown no longer exists
            .filter(key -> now.isAfter(cooldowns.get(key)) || !kits.containsKey(key.kit))
            .toList();
        toRemove.forEach(cooldowns::remove);
    }

    @Override
    protected void loadFrom(Configuration config) {
        kits.clear();
        kitNamesCache.clear();
        cooldowns.clear();
        //noinspection unchecked
        List<Kit> kitsList = (List<Kit>) config.getList("kits", List.of());
        kitsList.forEach(kit -> {
            kits.put(kit.name(), kit);
            kitNamesCache.put(kit.name(), MiniMessage.miniMessage().deserialize(kit.displayName()));
        });
        //noinspection unchecked
        List<KitCooldown> cooldownsList = (List<KitCooldown>) config.getList("cooldowns", List.of());
        cooldownsList.forEach(cooldown -> cooldowns.put(new CooldownKey(cooldown.player(), cooldown.kit()), cooldown.expires()));
        removeExpiredCooldowns();
        dropIfFull = config.getBoolean("dropIfFull", true);
    }

    @Override
    protected void saveTo(Configuration config) {
        List<Kit> kitsList = new ArrayList<>(kits.values());
        config.set("kits", kitsList);
        List<KitCooldown> cooldownsList = new ArrayList<>(cooldowns.size());
        removeExpiredCooldowns();
        cooldowns.forEach((key, expires) -> cooldownsList.add(new KitCooldown(key.player, key.kit, expires)));
        config.set("cooldowns", cooldownsList);
        config.set("dropIfFull", dropIfFull);
        config.setComments("dropIfFull", List.of(
            "Any items from a kit which cannot fit into a player's inventory will be...",
            "- true: dropped on the ground.",
            "- false: ignored and destroyed.",
            "You might want to consider disabling this if you are concerned about potential lag abuse."
        ));
    }

    @Nullable
    public Kit get(String name) {
        return kits.get(name);
    }

    public Collection<Kit> getAll() {
        return kits.values();
    }

    @Nullable
    public Component getDisplayName(String kit) {
        return kitNamesCache.get(kit);
    }

    @Nullable
    public Kit create(String name, List<SlottedItem> items) {
        if (!kits.containsKey(name)) {
            Kit kit = new Kit(name, items, name, 300);
            kits.put(name, kit);
            kitNamesCache.put(name, Component.text(name));
            return kit;
        }
        return null;
    }

    public void give(Player player, Kit kit) {
        PlayerInventory inv = player.getInventory();
        List<ItemStack> conflicts = new ArrayList<>();
        for (SlottedItem item : kit.items()) {
            ItemStack prevItem = inv.getItem(item.slot());
            if (prevItem != null && !prevItem.isEmpty()) {
                conflicts.add(item.item());
            } else {
                inv.setItem(item.slot(), item.item());
            }
        }
        Map<Integer, ItemStack> overflow = inv.addItem(conflicts.toArray(ItemStack[]::new));
        if (!overflow.isEmpty() && dropIfFull) {
            overflow.values().forEach(player::dropItem);
        }
    }

    public boolean give(Player player, String kitName) {
        Kit kit = kits.get(kitName);
        if (kit == null) {
            return false;
        }
        give(player, kit);
        return true;
    }

    public boolean delete(String name) {
        if (kits.remove(name) != null) {
            kitNamesCache.remove(name);
            List<CooldownKey> toRemove = cooldowns.keySet().stream()
                .filter(key -> key.kit.equals(name))
                .toList();
            toRemove.forEach(cooldowns::remove);
            return true;
        }
        return false;
    }

    public void update(Kit oldKit, Kit newKit) {
        boolean nameUpdated = !oldKit.name().equals(newKit.name());
        boolean displayNameUpdated = !oldKit.displayName().equals(newKit.displayName());
        if (nameUpdated) {
            kits.remove(oldKit.name());
            kitNamesCache.remove(oldKit.name());
        }
        kits.put(newKit.name(), newKit);
        if (displayNameUpdated) {
            kitNamesCache.put(newKit.name(), MiniMessage.miniMessage().deserialize(newKit.displayName()));
        }
        if (nameUpdated) {
            List<KitCooldown> newCooldowns = new ArrayList<>();
            List<CooldownKey> toRemove = new ArrayList<>();
            cooldowns.forEach((key, expires) -> {
                if (key.kit.equals(oldKit.name())) {
                    newCooldowns.add(new KitCooldown(key.player, newKit.name(), expires));
                    toRemove.add(key);
                }
            });
            // remove all old keys
            toRemove.forEach(cooldowns::remove);
            newCooldowns.forEach(cooldown -> {
                CooldownKey key = new CooldownKey(cooldown.player(), cooldown.kit());
                cooldowns.put(key, cooldown.expires());
            });
        }
    }

    // Cooldown stuff

    @Nullable
    public LocalDateTime getCooldownExpiration(Player player, String kit) {
        CooldownKey key = new CooldownKey(player.getUniqueId(), kit);
        LocalDateTime expires = cooldowns.get(key);
        if (expires != null && expires.isBefore(LocalDateTime.now())) {
            cooldowns.remove(key);
            expires = null;
        }
        return expires;
    }

    public void applyCooldown(Player player, Kit kit) {
        CooldownKey key = new CooldownKey(player.getUniqueId(), kit.name());
        cooldowns.put(key, LocalDateTime.now().plusSeconds(kit.cooldown()));
    }

    public boolean applyCooldown(Player player, String kitName) {
        Kit kit = kits.get(kitName);
        if (kit != null) {
            applyCooldown(player, kit);
            return true;
        }
        return false;
    }

    public boolean clearCooldown(Player player, String kitName) {
        CooldownKey key = new CooldownKey(player.getUniqueId(), kitName);
        return cooldowns.remove(key) != null;
    }

    private record CooldownKey(UUID player, String kit) {
    }

    public static Permission getKitPermission(String kitName) {
        return new Permission("wessentials.kit." + kitName);
    }

    public static Permission getKitPermission(Kit kit) {
        return getKitPermission(kit.name());
    }

}
