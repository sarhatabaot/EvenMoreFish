package com.oheers.fish.config;

import com.oheers.fish.EvenMoreFish;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

public class GUIConfig extends ConfigBase {

    private static GUIConfig instance = null;

    public GUIConfig() {
        super("guis.yml", "guis.yml", EvenMoreFish.getInstance(), true);
        instance = this;
    }
    
    public static GUIConfig getInstance() {
        return instance;
    }

    @Override
    protected boolean postLoad() {
        boolean changed = false;
        YamlDocument document = getConfig();

        // Remove competition menu button
        String competitionsKey = "main-menu.coming-soon-competitions";
        if (document.contains(competitionsKey)) {
            document.remove(competitionsKey);
            changed = true;
        }

        return changed;
    }

}
