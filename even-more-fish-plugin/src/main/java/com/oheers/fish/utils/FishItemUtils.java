package com.oheers.fish.utils;

import com.oheers.fish.exceptions.InvalidFishException;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.FishManager;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.utils.nbt.NbtKeys;
import com.oheers.fish.utils.nbt.NbtUtils;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Skull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class FishItemUtils {

    private FishItemUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isFish(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return false;
        }
        return NbtUtils.hasKey(item, NbtKeys.EMF_FISH_NAME);
    }

    public static boolean isFish(Skull skull) {
        if (skull == null) {
            return false;
        }
        return NbtUtils.hasKey(skull, NbtKeys.EMF_FISH_NAME);
    }

    public static @Nullable Fish getFish(ItemStack item) {
        if (!isFish(item)) {
            return null;
        }

        String nameString = NbtUtils.getString(item, NbtKeys.EMF_FISH_NAME);
        String playerString = NbtUtils.getString(item, NbtKeys.EMF_FISH_PLAYER);
        String rarityString = NbtUtils.getString(item, NbtKeys.EMF_FISH_RARITY);
        Float lengthFloat = NbtUtils.getFloat(item, NbtKeys.EMF_FISH_LENGTH);
        Integer randomIndex = NbtUtils.getInteger(item, NbtKeys.EMF_FISH_RANDOM_INDEX);

        return createFishFromData(nameString, playerString, rarityString, lengthFloat, randomIndex);
    }

    public static @Nullable Fish getFish(Skull skull, Player fisher) throws InvalidFishException {
        if (!isFish(skull)) {
            return null;
        }

        final String nameString = NBT.getPersistentData(skull, nbt -> nbt.getString(NbtUtils.getNamespacedKey(NbtKeys.EMF_FISH_NAME).toString()));
        final String playerString = NBT.getPersistentData(skull, nbt -> nbt.getString(NbtUtils.getNamespacedKey(NbtKeys.EMF_FISH_PLAYER).toString()));
        final String rarityString = NBT.getPersistentData(skull, nbt -> nbt.getString(NbtUtils.getNamespacedKey(NbtKeys.EMF_FISH_RARITY).toString()));
        final Float lengthFloat = NBT.getPersistentData(skull, nbt -> nbt.getFloat(NbtUtils.getNamespacedKey(NbtKeys.EMF_FISH_LENGTH).toString()));
        final Integer randomIndex = NBT.getPersistentData(skull, nbt -> nbt.getInteger(NbtUtils.getNamespacedKey(NbtKeys.EMF_FISH_RANDOM_INDEX).toString()));

        if (nameString == null || rarityString == null) {
            throw new InvalidFishException("NBT Error");
        }

        Fish fish = createFishFromData(nameString, playerString, rarityString, lengthFloat, randomIndex);
        if (fish != null && playerString == null && fisher != null) {
            fish.setFisherman(fisher.getUniqueId());
        }
        return fish;
    }

    private static @Nullable Fish createFishFromData(String name, String playerUUID, String rarityName, Float length, Integer randomIndex) {
        Rarity rarity = FishManager.getInstance().getRarity(rarityName);
        if (rarity == null) {
            return null;
        }

        Fish fish = rarity.getFish(name);
        if (fish == null) {
            return null;
        }

        fish.setLength(length);
        if (randomIndex != null) {
            fish.getFactory().setRandomIndex(randomIndex);
        }
        if (playerUUID != null) {
            try {
                fish.setFisherman(UUID.fromString(playerUUID));
            } catch (IllegalArgumentException exception) {
                fish.setFisherman(null);
            }
        }
        return fish;
    }

    public static boolean isBaitObject(ItemStack item) {
        return item != null && item.getItemMeta() != null && NbtUtils.hasKey(item, NbtKeys.EMF_BAIT);
    }
}
