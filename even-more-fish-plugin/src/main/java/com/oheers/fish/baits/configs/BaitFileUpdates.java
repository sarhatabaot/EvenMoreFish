package com.oheers.fish.baits.configs;

import com.oheers.fish.FishUtils;
import com.oheers.fish.baits.BaitHandler;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;

public class BaitFileUpdates {

    public static void update(@NotNull BaitHandler bait) {
        YamlDocument config = bait.getConfig();

        // bait-theme -> format
        if (config.contains("bait-theme")) {
            String theme = config.getString("bait-theme");
            String format = FishUtils.getFormat(theme);

            config.set("format", format);
            // Remove the old key
            config.remove("bait-theme");
        }

        bait.save();
    }

}
