package com.oheers.fish.baits.manager;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.api.AbstractFileBasedManager;
import com.oheers.fish.baits.Bait;
import com.oheers.fish.baits.configs.BaitConversions;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.fishing.items.FishManager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BaitManager extends AbstractFileBasedManager<Bait> {

    private static BaitManager instance;

    private BaitManager() {
        super();
    }

    public static BaitManager getInstance() {
        if (instance == null) {
            instance = new BaitManager();
        }
        return instance;
    }

    @Override
    protected void performPreLoadConversions() {
        new BaitConversions().performCheck();
    }

    @Override
    protected void loadItems() {
        loadItemsFromFiles(
                "baits",
                file -> new Bait(file, FishManager.getInstance(), MainConfig.getInstance()),
                Bait::getId,
                bait -> {
                    if (bait.isDisabled()) {
                        EvenMoreFish.getInstance().debug("Skipping disabled bait: " + bait.getId());
                        return true;
                    }
                    return false;
                }
        );
    }

    @Override
    protected void logLoadedItems() {
        EvenMoreFish.getInstance().getLogger().info(
                "Loaded " + getItemMap().size() + " baits successfully."
        );
    }

    public @Nullable Bait getBait(@Nullable String baitName) {
        return baitName != null ? getItem(baitName) : null;
    }

    public @Nullable Bait getBait(@Nullable ItemStack itemStack) {
        return itemStack != null ? getBait(BaitNBTManager.getBaitName(itemStack)) : null;
    }
}