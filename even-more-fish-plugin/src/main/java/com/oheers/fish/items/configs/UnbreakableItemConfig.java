package com.oheers.fish.items.configs;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

public class UnbreakableItemConfig extends ItemConfig<@NotNull Boolean> {

    public UnbreakableItemConfig(@NotNull Section section) {
        super(section);
    }

    @Override
    public @NotNull Boolean getConfiguredValue() {
        return section.getBoolean("item.unbreakable", false);
    }

    @Override
    protected BiConsumer<ItemStack, @NotNull Boolean> applyToItem(@Nullable Map<String, ?> replacements) {
        return (item, value) -> item.editMeta(meta -> meta.setUnbreakable(value));
    }

}
