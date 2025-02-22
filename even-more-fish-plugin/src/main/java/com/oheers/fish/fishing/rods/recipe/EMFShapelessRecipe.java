package com.oheers.fish.fishing.rods.recipe;

import com.oheers.fish.EvenMoreFish;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EMFShapelessRecipe {

    private final ShapelessRecipe recipe;
    private final List<String> ingredients;
    private boolean registered = false;

    public EMFShapelessRecipe(@NotNull NamespacedKey key, @NotNull ItemStack result, @NotNull List<String> ingredients) {
        if (ingredients.isEmpty()) {
            throw new RuntimeException("Shapeless recipe is missing ingredients.");
        }
        this.ingredients = ingredients;
        this.recipe = new ShapelessRecipe(key, result);
        prepareRecipe();
    }

    private void prepareRecipe() {
        this.ingredients.forEach(ingredient -> {
            RecipeChoice choice = RecipeUtil.getRecipeChoice(ingredient);
            if (choice != null) {
                recipe.addIngredient(choice);
            }
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
