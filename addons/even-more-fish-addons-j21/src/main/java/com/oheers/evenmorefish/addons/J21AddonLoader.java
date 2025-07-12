package com.oheers.evenmorefish.addons;

import com.oheers.fish.api.addons.AddonLoader;
import com.oheers.fish.api.addons.AddonMetadata;
import com.oheers.fish.api.utils.system.JavaSpecVersion;
import com.oheers.fish.api.utils.system.SystemUtils;


public class J21AddonLoader extends AddonLoader {
    @Override
    public AddonMetadata getAddonMetadata() {
        return new AddonMetadata("Java 21 Addons", "1.1.0", "EvenMoreFish");
    }

    @Override
    public boolean canLoad() {
        return SystemUtils.isJavaVersionAtLeast(JavaSpecVersion.JAVA_21);
    }

    @Override
    public void loadAddons() {
        // ItemAddon
        new CraftEngineItemAddon().register();
        new NexoItemAddon().register();
        new OraxenItemAddon().register();
    }

}
