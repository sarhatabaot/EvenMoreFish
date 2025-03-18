package com.oheers.fish.api.reward;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated This class will be removed for EvenMoreFish 2.1.0.
 */
@Deprecated(forRemoval = true, since = "2.0.0")
public class EMFRewardsLoadEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

}
