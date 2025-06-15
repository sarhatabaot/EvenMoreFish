package com.oheers.fish;

import com.oheers.fish.config.MainConfig;
import com.oheers.fish.fishing.rods.CustomRod;
import com.oheers.fish.fishing.rods.RodManager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * A utility class for common checks
 */
public class Checks {

    public static boolean canUseRod(@Nullable ItemStack item) {
        if (item == null) {
            System.out.println("Item was null");
            return false;
        }
        if (!MainConfig.getInstance().requireCustomRod()) {
            System.out.println("Custom rod not required");
            return true;
        }
        CustomRod customRod = RodManager.getInstance().getRod(item);
        System.out.println("Is custom rod? " + (customRod != null));
        return customRod != null;
    }

}
