package com.oheers.evenmorefish.addons;

import com.oheers.fish.api.addons.AddonLoader;
import com.oheers.fish.api.addons.AddonMetadata;
import com.oheers.fish.api.plugin.EMFPlugin;
import com.oheers.fish.api.utils.system.JavaSpecVersion;
import com.oheers.fish.api.utils.system.SystemUtils;


public class J21AddonLoader extends AddonLoader {
    public J21AddonLoader(EMFPlugin plugin) {
        super(plugin);
    }

    @Override
    public AddonMetadata getAddonMetadata() {
        return new AddonMetadata("Java 21 Addons", "1.1.0", "EvenMoreFish", "Bundled Java 21 Addons");
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
