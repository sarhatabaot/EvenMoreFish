package com.oheers.fish.recipe;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.FishUtils;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EMFShapedRecipe {

    private final ShapedRecipe recipe;
    private final List<String> rawShape;
    private final Section ingredientsSection;
    private boolean registered = false;

    public EMFShapedRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result, @NotNull Section section) {
        this.rawShape = section.getStringList("shape");
        this.ingredientsSection = section.getSection("ingredients");
        if (this.rawShape.isEmpty() || this.ingredientsSection == null) {
            throw new RuntimeException("Shaped recipe is missing shape or ingredients.");
        }
        this.recipe = new ShapedRecipe(key, result);
        prepareRecipe();
    }

    private void prepareRecipe() {
        String[] shape = rawShape.stream().limit(3).toArray(String[]::new);
        recipe.shape(shape);

        ingredientsSection.getRoutesAsStrings(false).forEach(key -> {
            char character = FishUtils.getCharFromString(key, '#');
            String materialStr = ingredientsSection.getString(key);

            if (materialStr == null) {
                // If invalid material string, just skip it.
                return;
            }

            RecipeChoice choice = RecipeUtil.getRecipeChoice(materialStr);
            if (choice == null) {
                // If a recipe choice could not be created, just skip it.
                return;
            }
            recipe.setIngredient(character, choice);
        });
    }

    public boolean isRegistered() {
        return this.registered;
    }

    public void register() {
        if (isRegistered()) {
            throw new RuntimeException("Attempted to register a recipe that is already registered.");
        }
        EvenMoreFish.getInstance().getServer().addRecipe(recipe);
        this.registered = true;
    }

    public void unregister() {
        if (!isRegistered()) {
            throw new RuntimeException("Attempted to unregister a recipe that is not registered.");
        }
        EvenMoreFish.getInstance().getServer().removeRecipe(recipe.getKey());
        this.registered = false;
    }

}
