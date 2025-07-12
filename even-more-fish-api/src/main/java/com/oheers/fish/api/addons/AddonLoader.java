package com.oheers.fish.api.addons;

import com.oheers.fish.api.plugin.EMFPlugin;

public abstract class AddonLoader {

    public abstract AddonMetadata getAddonMetadata();


    public abstract void loadAddons();

    public final void load() {
        if (!canLoad()) {
            return;
        }

        loadAddons();
        EMFPlugin.getInstance().debug("Loaded Addon %s %s by %s".formatted(getAddonMetadata().name(), getAddonMetadata().version(), getAddonMetadata().author()));
    }

    public abstract boolean canLoad();

}
