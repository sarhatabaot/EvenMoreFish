package com.oheers.fish.api.reward;

import com.oheers.fish.api.plugin.EMFPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated This class will be removed for EvenMoreFish 2.1.0. Please use the static methods on {@link RewardType} instead.
 */
@Deprecated(forRemoval = true, since = "2.0.0")
public class RewardManager implements Listener {

    private static RewardManager instance = null;

    private RewardManager() {}

    public static RewardManager getInstance() {
        if (instance == null) {
            instance = new RewardManager();
        }
        return instance;
    }

    /**
     * This will always return true as the new system can never be disabled.
     */
    public boolean isLoaded() { return true; }

    /**
     * @deprecated Use {@link RewardType#register()} instead.
     */
    @Deprecated(forRemoval = true, since = "2.0.0")
    public boolean registerRewardType(RewardType rewardType) {
        rewardType.register();
        return true;
    }

    /**
     * @deprecated Use {@link RewardType#getLoadedTypes()} and {@link Map#values()} instead.
     */
    @Deprecated(forRemoval = true, since = "2.0.0")
    public List<RewardType> getRegisteredRewardTypes() {
        return new ArrayList<>(RewardType.getLoadedTypes().values());
    }

}
