package com.oheers.fish.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.oheers.fish.api.addons.ItemAddon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class ItemGenerationUtils {

    private ItemGenerationUtils() {
        throw new UnsupportedOperationException();
    }

    public static @Nullable ItemStack getCustomItem(@NotNull String materialString) {
        if (!materialString.contains(":")) {
            return null;
        }
        try {
            final String[] split = materialString.split(":", 2);
            return ItemAddon.getItem(split[0], split[1]);
        } catch (ArrayIndexOutOfBoundsException exception) {
            return null;
        }
    }

    public static @Nullable ItemStack getItem(@NotNull final String materialString) {
        if (materialString.contains(":")) {
            return getCustomItem(materialString);
        }

        Material material = ItemUtils.getMaterial(materialString);
        return material == null ? null : new ItemStack(material);
    }

    public static @NotNull ItemStack getSkullFromBase64(String base64) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        skull.editMeta(SkullMeta.class, meta -> {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "EMFSkull");
            profile.setProperty(new ProfileProperty("textures", base64));
            meta.setPlayerProfile(profile);
        });
        return skull;
    }

    public static @NotNull ItemStack getSkullFromUUID(UUID uuid) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        skull.editMeta(SkullMeta.class, meta -> {
            PlayerProfile profile = Bukkit.createProfile(uuid, "EMFSkull");
            meta.setPlayerProfile(profile);
        });
        return skull;
    }

    public static @NotNull ItemStack getSkullFromUUIDString(@NotNull String uuidString) {
        try {
            return getSkullFromUUID(UUID.fromString(uuidString));
        } catch (IllegalArgumentException exception) {
            return new ItemStack(Material.PLAYER_HEAD);
        }
    }

    public static PotionEffect getPotionEffect(@NotNull String effectString) {
        String[] split = effectString.split(":");
        if (split.length != 3) {
            Logging.error("Potion effect string is formatted incorrectly. Use \"potion:duration:amplifier\".");
            return null;
        }
        PotionEffectType type = PotionEffectType.getByName(split[0]);
        if (type == null) {
            Logging.error("Potion effect type " + split[0] + " is not valid.");
            return null;
        }
        Integer duration = NumberUtils.parseInteger(split[1]);
        if (duration == null) {
            Logging.error("Potion effect duration " + split[1] + " is not valid.");
            return null;
        }
        Integer amplifier = NumberUtils.parseInteger(split[2]);
        if (amplifier == null) {
            Logging.error("Potion effect amplifier " + split[2] + " is not valid.");
            return null;
        }
        return new PotionEffect(
                type,
                duration * 20,
                amplifier - 1,
                false
        );
    }
}