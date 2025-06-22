package com.oheers.fish.fishing;

import com.oheers.fish.Checks;
import com.oheers.fish.utils.FishUtils;
import com.oheers.fish.api.EMFFishEvent;
import com.oheers.fish.competition.Competition;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.permissions.UserPerms;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

public class FishingProcessor extends Processor<PlayerFishEvent> {

    @Override
    @EventHandler(priority = EventPriority.HIGHEST)
    public void process(@NotNull PlayerFishEvent event) {
        ItemStack rod = getRod(event);

        if (!isCustomFishAllowed(event.getPlayer()) || !Checks.canUseRod(rod)) {
            return;
        }

        if (MainConfig.getInstance().requireFishingPermission() && !event.getPlayer().hasPermission(UserPerms.USE_ROD)) {
            if (event.getState() == PlayerFishEvent.State.FISHING) {//send msg only when throw the lure
                ConfigMessage.NO_PERMISSION_FISHING.getMessage().send(event.getPlayer());
            }
            return;
        }

        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        ItemStack fish = getFish(event.getPlayer(), event.getHook().getLocation(), rod);

        if (fish == null) {
            return;
        }

        if (!(event.getCaught() instanceof Item nonCustom)) {
            return;
        }

        if (MainConfig.getInstance().giveStraightToInventory() && isSpaceForNewFish(event.getPlayer().getInventory())) {
            FishUtils.giveItem(fish, event.getPlayer());
            nonCustom.remove();
        } else {
            // replaces the fishing item with a custom evenmorefish fish.
            if (fish.getType().isAir()) {
                nonCustom.remove();
            } else {
                nonCustom.setItemStack(fish);
            }
        }

    }

    @Override
    protected boolean isEnabled() {
        return MainConfig.getInstance().isCatchEnabled();
    }

    @Override
    protected boolean competitionOnlyCheck() {
        Competition active = Competition.getCurrentlyActive();

        if (active != null) {
            return active.getCompetitionFile().isAllowFishing();
        }

        return !MainConfig.getInstance().isFishCatchOnlyInCompetition();
    }


    @Override
    protected boolean fireEvent(@NotNull Fish fish, @NotNull Player player) {
        EMFFishEvent fishEvent = new EMFFishEvent(fish, player, LocalDateTime.now());
        return fishEvent.callEvent();
    }

    @Override
    protected ConfigMessage getCaughtMessage() {
        return ConfigMessage.FISH_CAUGHT;
    }

    @Override
    protected ConfigMessage getLengthlessCaughtMessage() {
        return ConfigMessage.FISH_LENGTHLESS_CAUGHT;
    }

    @Override
    protected boolean shouldCatchBait() {
        return true;
    }

    @Override
    public boolean canUseFish(@NotNull Fish fish) {
        return fish.getCatchType().equals(CatchType.CATCH)
                || fish.getCatchType().equals(CatchType.BOTH);
    }

    private ItemStack getRod(@NotNull PlayerFishEvent event) {
        EquipmentSlot hand = event.getHand();
        if (hand == null) {
            return null;
        }
        return event.getPlayer().getInventory().getItem(hand);
    }

}