package me.whizvox.wessentials.module.kit;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;
import java.util.Map;

@NotNullByDefault
public record Kit(String name,
                  List<SlottedItem> items,
                  String displayName,
                  int cooldown) implements ConfigurationSerializable {

    public Kit(Map<String, Object> obj) {
        //noinspection unchecked
        this(
            (String) obj.get("name"),
            (List<SlottedItem>) obj.get("items"),
            (String) obj.get("displayName"),
            (Integer) obj.get("cooldown")
        );
    }

    public Kit updateName(String newName) {
        return new Kit(newName, items, displayName, cooldown);
    }

    public Kit updateDisplayName(String newDisplayName) {
        return new Kit(name, items, newDisplayName, cooldown);
    }

    public Kit updateItems(List<SlottedItem> newItems) {
        return new Kit(name, newItems, displayName, cooldown);
    }

    public Kit updateCooldown(int newCooldown) {
        return new Kit(name, items, displayName, newCooldown);
    }

    @Override
    public Map<String, Object> serialize() {
        return Map.of(
            "name", name,
            "displayName", displayName,
            "cooldown", cooldown,
            "items", items
        );
    }

}
