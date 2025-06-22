package com.oheers.fish.selling;

import com.oheers.fish.utils.FishUtils;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.FishManager;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.utils.nbt.NbtKeys;
import com.oheers.fish.utils.nbt.NbtUtils;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class WorthNBT {

    private WorthNBT() {
        throw new UnsupportedOperationException();
    }

    public static ItemStack setNBT(@NotNull ItemStack fishItem, @NotNull Fish fish) {
        if (NbtUtils.isInvalidItem(fishItem)) {
            return fishItem;
        }
        // creates key and plops in the value of "value"
        NBT.modify(fishItem, nbt -> {
            ReadWriteNBT emfCompound = nbt.getOrCreateCompound(NbtKeys.EMF_COMPOUND);
            if (fish.getLength() > 0) {
                emfCompound.setFloat(NbtKeys.EMF_FISH_LENGTH, fish.getLength());
            }
            if (!fish.hasFishermanDisabled() && fish.getFisherman() != null) {
                emfCompound.setString(NbtKeys.EMF_FISH_PLAYER, fish.getFisherman().toString());
            }
            emfCompound.setString(NbtKeys.EMF_FISH_NAME, fish.getName());
            emfCompound.setString(NbtKeys.EMF_FISH_RARITY, fish.getRarity().getId());
            emfCompound.setInteger(NbtKeys.EMF_FISH_RANDOM_INDEX, fish.getFactory().getRandomIndex());
        });

        return fishItem;
    }

    public static void setNBT(Skull fishSkull, Fish fish) {
        NamespacedKey nbtlength = NbtUtils.getNamespacedKey(NbtKeys.EMF_FISH_LENGTH);
        NamespacedKey nbtplayer = NbtUtils.getNamespacedKey(NbtKeys.EMF_FISH_PLAYER);
        NamespacedKey nbtrarity = NbtUtils.getNamespacedKey(NbtKeys.EMF_FISH_RARITY);
        NamespacedKey nbtname = NbtUtils.getNamespacedKey(NbtKeys.EMF_FISH_NAME);
        NamespacedKey nbtrandomIndex = NbtUtils.getNamespacedKey(NbtKeys.EMF_FISH_RANDOM_INDEX);

        //TODO try with NBT-API
        PersistentDataContainer itemMeta = fishSkull.getPersistentDataContainer();

        if (fish.getLength() > 0) {
            itemMeta.set(nbtlength, PersistentDataType.FLOAT, fish.getLength());
        }
        if (fish.getFisherman() != null && !fish.hasFishermanDisabled()) {
            itemMeta.set(nbtplayer, PersistentDataType.STRING, fish.getFisherman().toString());
        }
        itemMeta.set(nbtrandomIndex, PersistentDataType.INTEGER, fish.getFactory().getRandomIndex());
        itemMeta.set(nbtrarity, PersistentDataType.STRING, fish.getRarity().getId());
        itemMeta.set(nbtname, PersistentDataType.STRING, fish.getName());
    }

    public static double getValue(@NotNull ItemStack item) {
        if (NbtUtils.isInvalidItem(item)) {
            return -1.0;
        }

        // creating the key to check for
        if (!FishUtils.isFish(item)) {
            return -1.0;
        }

        // it's a fish so it'll definitely have these NBT values
        Float length = NbtUtils.getFloat(item, NbtKeys.EMF_FISH_LENGTH);
        String rarityStr = NbtUtils.getString(item, NbtKeys.EMF_FISH_RARITY);
        String name = NbtUtils.getString(item, NbtKeys.EMF_FISH_NAME);

        // gets a possible set-worth in the fish config
        try {
            Rarity rarity = FishManager.getInstance().getRarity(rarityStr);
            Fish fish = rarity.getFish(name);
            double value = fish.getSetWorth();

            if (value == 0) {
                throw new NullPointerException();
            }
            return value;
        } catch (NullPointerException npe) {
            // there's no set-worth so we're calculating the worth ourselves
            return length != null && length > 0 ? getMultipliedValue(length, rarityStr, name) : 0;
        }
    }

    private static double getMultipliedValue(Float length, String rarity, String name) {
        return getWorthMultiplier(rarity, name) * length;
    }

    private static double getWorthMultiplier(final String rarityStr, final String name) {
        Rarity rarity = FishManager.getInstance().getRarity(rarityStr);
        if (rarity == null) {
            return 0.0D;
        }
        Fish fish = rarity.getFish(name);
        if (fish == null) {
            return 0.0D;
        }
        double value = fish.getWorthMultiplier();
        // Is there a value set for the specific fish?
        if (value == 0.0) {
            return rarity.getWorthMultiplier();
        }

        return value;
    }

}
