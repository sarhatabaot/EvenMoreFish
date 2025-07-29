package com.oheers.fish.config;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigUtils {

    public static @NotNull Section getOrCreateSection(@NotNull Section section, @NotNull String path) {
        Section subSection = section.getSection(path);
        if (subSection == null) {
            subSection = section.createSection(path);
        }
        return subSection;
    }

    /**
     * Gets the first section of many paths.
     * Useful for typos... Oops
     */
    public static @Nullable Section getSectionOfMany(@NotNull Section section, @NotNull String... paths) {
        for (String path : paths) {
            Section sub = section.getSection(path);
            if (sub != null) {
                return sub;
            }
        }
        return null;
    }

}
