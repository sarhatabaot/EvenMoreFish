package com.oheers.fish.fishing.rods.recipe;

import com.oheers.fish.FishUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Nullable;

public class RecipeUtil {

    public static @Nullable RecipeChoice getRecipeChoice(String materialStr) {
        ItemStack customItem = FishUtils.getItem(materialStr);
        if (customItem == null || customItem.getType().isAir()) {
            return null;
        }
        return new RecipeChoice.ExactChoice(customItem);
    }

}
