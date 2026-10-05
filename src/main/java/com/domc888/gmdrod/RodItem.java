package com.domc888.gmdrod;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class RodItem {

    public record Binding(UUID targetId, String targetName, GameMode mode) {
    }

    private final NamespacedKey uuidKey;
    private final NamespacedKey nameKey;
    private final NamespacedKey modeKey;

    public RodItem(GMDRod plugin) {
        this.uuidKey = new NamespacedKey(plugin, "target_uuid");
        this.nameKey = new NamespacedKey(plugin, "target_name");
        this.modeKey = new NamespacedKey(plugin, "mode");
    }

    public ItemStack create(Player target, GameMode mode) {
        ItemStack item = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(plain("GM Rod"));
        meta.lore(List.of(
                plain("Target: " + target.getName()),
                plain("Mode: " + mode.name().toLowerCase(Locale.ROOT)),
                plain("Cast, then reel in to apply")));
        meta.setUnbreakable(true);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(uuidKey, PersistentDataType.STRING, target.getUniqueId().toString());
        pdc.set(nameKey, PersistentDataType.STRING, target.getName());
        pdc.set(modeKey, PersistentDataType.STRING, mode.name());

        item.setItemMeta(meta);
        return item;
    }

    public Binding read(ItemStack item) {
        if (item == null || item.getType() != Material.FISHING_ROD || !item.hasItemMeta()) {
            return null;
        }

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        String uuid = pdc.get(uuidKey, PersistentDataType.STRING);
        String name = pdc.get(nameKey, PersistentDataType.STRING);
        String mode = pdc.get(modeKey, PersistentDataType.STRING);
        if (uuid == null || name == null || mode == null) {
            return null;
        }

        try {
            return new Binding(UUID.fromString(uuid), name, GameMode.valueOf(mode));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Component plain(String text) {
        return Component.text(text).decoration(TextDecoration.ITALIC, false);
    }
}
