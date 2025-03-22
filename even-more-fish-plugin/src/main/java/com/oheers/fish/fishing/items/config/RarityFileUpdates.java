package com.oheers.fish.fishing.items.config;

import com.oheers.fish.fishing.items.Rarity;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;

public class RarityFileUpdates {

    public static void update(@NotNull Rarity rarity) {
        YamlDocument config = rarity.getConfig();
        if (config.contains("colour")) {
            config.set("format", config.getString("colour") + "{name}");
            config.remove("colour");
        }

        rarity.save();
    }

}
