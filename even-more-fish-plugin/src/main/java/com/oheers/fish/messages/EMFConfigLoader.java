package com.oheers.fish.messages;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.messagelib.config.ConfigLoader;

import java.util.List;

public class EMFConfigLoader implements ConfigLoader<Section> {

    private final Section config;

    public EMFConfigLoader(@NotNull Section section) {
        this.config = section;
    }
    
    @Override
    public @Nullable Object getObject(String path) {
        return config.get(path);
    }

    @Override
    public @Nullable String getString(String path) {
        return config.getString(path);
    }

    @Override
    public @NotNull List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    @Override
    public @NotNull Section getConfig() {
        return config;
    }

    @Override
    public @Nullable ConfigLoader<Section> getSection(@NotNull String path) {
        Section section = config.getSection(path);
        return section == null ? null : new EMFConfigLoader(section);
    }
    
}
