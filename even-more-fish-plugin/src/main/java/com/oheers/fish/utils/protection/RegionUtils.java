package com.oheers.fish.utils.protection;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.config.MainConfig;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class RegionUtils {

    private RegionUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean checkRegion(Location location, @NotNull List<String> whitelistedRegions) {
        if (whitelistedRegions.isEmpty()) {
            return true;
        }

        if (isPluginEnabled("WorldGuard")) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet regions = query.getApplicableRegions(BukkitAdapter.adapt(location));

            for (ProtectedRegion region : regions) {
                if (whitelistedRegions.contains(region.getId())) {
                    return true;
                }
            }
            return false;
        }

        if (isPluginEnabled("RedProtect")) {
            Region region = RedProtect.get().getAPI().getRegion(location);
            return region != null && whitelistedRegions.contains(region.getName());
        }

        EvenMoreFish.getInstance().getLogger().warning("Please install WorldGuard or RedProtect to use allowed-regions.");
        return true;
    }

    public static @Nullable String getRegionName(Location location) {
        if (!MainConfig.getInstance().isRegionBoostsEnabled()) {
            return null;
        }

        if (isPluginEnabled("WorldGuard")) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            ApplicableRegionSet set = container.createQuery().getApplicableRegions(BukkitAdapter.adapt(location));
            return set.getRegions().isEmpty() ? null : set.iterator().next().getId();
        }

        if (isPluginEnabled("RedProtect")) {
            Region region = RedProtect.get().getAPI().getRegion(location);
            return region == null ? null : region.getName();
        }

        EvenMoreFish.getInstance().getLogger().warning("Please install WorldGuard or RedProtect to use region-boosts.");
        return null;
    }

    private static boolean isPluginEnabled(String pluginName) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }
}
