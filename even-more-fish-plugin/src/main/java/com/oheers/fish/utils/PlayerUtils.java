package com.oheers.fish.utils;

import com.oheers.fish.EvenMoreFish;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PlayerUtils {

    private PlayerUtils() {
        throw new UnsupportedOperationException();
    }

    public static void giveItems(List<ItemStack> items, Player player) {
        if (items == null || items.isEmpty()) {
            return;
        }

        List<ItemStack> filteredItems = items.stream()
                .filter(Objects::nonNull)
                .toList();

        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.5f);

        Map<Integer, ItemStack> leftoverItems = player.getInventory().addItem(filteredItems.toArray(new ItemStack[0]));
        leftoverItems.values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
    }

    public static boolean isHoldingMaterial(@NotNull Player player, @NotNull Material material) {
        return player.getInventory().getItemInMainHand().getType().equals(material)
                || player.getInventory().getItemInOffHand().getType().equals(material);
    }

    public static @Nullable String getPlayerName(@Nullable UUID uuid) {
        if (uuid == null) {
            return null;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return player.getName();
    }

    public static @Nullable String getPlayerName(@Nullable String uuidString) {
        if (uuidString == null) {
            return null;
        }
        try {
            return getPlayerName(UUID.fromString(uuidString));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
