package com.oheers.fish.fishing.items;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.FishUtils;
import com.oheers.fish.api.FileUtil;
import com.oheers.fish.api.plugin.EMFPlugin;
import com.oheers.fish.api.requirement.Requirement;
import com.oheers.fish.api.requirement.RequirementContext;
import com.oheers.fish.competition.Competition;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.fishing.Processor;
import com.oheers.fish.fishing.items.config.FishConversions;
import com.oheers.fish.fishing.items.config.RarityConversions;
import com.oheers.fish.fishing.rods.CustomRod;
import com.oheers.fish.utils.WeightedRandom;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

public class FishManager {

    private static FishManager instance;

    private final TreeMap<String, Rarity> rarityMap;
    private boolean loaded = false;

    private FishManager() {
        rarityMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        new RarityConversions().performCheck();
        new FishConversions().performCheck();
    }

    public static FishManager getInstance() {
        if (instance == null) {
            instance = new FishManager();
        }
        return instance;
    }

    public void load() {
        if (isLoaded()) {
            return;
        }
        loadRarities();
        logLoadedItems();
        loaded = true;
    }

    public void reload() {
        if (!isLoaded()) {
            return;
        }
        rarityMap.clear();
        loadRarities();
        logLoadedItems();
    }

    public void unload() {
        if (!isLoaded()) {
            return;
        }
        rarityMap.clear();
        loaded = false;
    }

    public boolean isLoaded() {
        return loaded;
    }

    // Getters for Rarities and Fish

    public @Nullable Rarity getRarity(@NotNull String rarityName) {
        return rarityMap.get(rarityName);
    }

    public @Nullable Fish getFish(@NotNull String rarityName, @NotNull String fishName) {
        Rarity rarity = getRarity(rarityName);
        if (rarity == null) {
            return null;
        }
        return rarity.getFish(fishName);
    }

    private Rarity getPreDecidedRarity(Player player) {
        if (player == null) return null;

        Map<UUID, Rarity> decidedRarities = EvenMoreFish.getInstance().getDecidedRarities();
        return decidedRarities.remove(player.getUniqueId());
    }

    private boolean isRarityAllowedInCompetition(Rarity rarity) {
        boolean isCompetitionActive = Competition.isActive();
        boolean isCompExempt = EvenMoreFish.getInstance().isRaritiesCompCheckExempt();
        boolean hasExemptFish = rarity.hasCompExemptFish();

        // Case 1: Competition is inactive & rarity has exempt fish
        if (!isCompetitionActive && isCompExempt) {
            return hasExemptFish;
        }

        // Case 2: Always allowed if competition is active OR config allows non-competition catches
        return isCompetitionActive || !MainConfig.getInstance().isFishCatchOnlyInCompetition();
    }

    private Rarity selectRandomRarity(List<Rarity> allowedRarities, double boostRate,
                                      Set<Rarity> boostedRarities) {
        return WeightedRandom.pick(
                allowedRarities,
                Rarity::getWeight,
                boostRate,
                boostedRarities,
                EvenMoreFish.getInstance().getRandom()
        );
    }

    public Rarity getRandomWeightedRarity(Player fisher, double boostRate,
                                          @NotNull Set<Rarity> boostedRarities,
                                          Set<Rarity> totalRarities,
                                          @Nullable CustomRod customRod) {
        // 1. Check pre-decided rarity
        Rarity preDecided = getPreDecidedRarity(fisher);
        if (preDecided != null) return preDecided;

        // 2. Get allowed rarities (with your original logic)
        List<Rarity> allowedRarities = getAllowedRarities(fisher, boostRate, boostedRarities, totalRarities);

        // 3. Apply custom rod filter (new requirement)
        allowedRarities = filterByCustomRod(allowedRarities, customRod);

        // 4. Fail fast if no rarities left
        if (allowedRarities.isEmpty()) {
            String fisherName = fisher == null ? "N/A" : fisher.getName();
            EvenMoreFish.getInstance().getLogger().severe(
                    "No rarities available for " + fisherName + ". They received no fish."
            );
            return null;
        }

        // 5. Pick a random rarity (weighted)
        Rarity selected = selectRandomRarity(allowedRarities, boostRate, boostedRarities);
        if (selected == null) return null;

        // 6. Apply competition rules
        return isRarityAllowedInCompetition(selected) ? selected : null;
    }

    private List<Rarity> filterByCustomRod(List<Rarity> rarities, @Nullable CustomRod customRod) {
        if (customRod == null) return rarities;
        return rarities.stream()
                .filter(rarity -> customRod.getAllowedRarities().contains(rarity))
                .toList();
    }

    private @NotNull List<Rarity> getAllowedRarities(
            Player fisher,
            double boostRate,
            Set<Rarity> boostedRarities,
            Set<Rarity> totalRarities
    ) {
        if (fisher == null) {
            return new ArrayList<>(totalRarities);
        }

        RequirementContext context = new RequirementContext(
                fisher.getWorld(),
                fisher.getLocation(),
                fisher,
                null,
                null
        );

        String region = FishUtils.getRegionName(fisher.getLocation());
        MainConfig config = MainConfig.getInstance();

        return rarityMap.values().stream()
                .filter(rarity -> !shouldSkipRarity(rarity, boostRate, boostedRarities, fisher))
                .filter(rarity -> rarity.getRequirement().meetsRequirements(context))
                .flatMap(rarity -> {
                    double regionBoost = config.getRegionBoost(region, rarity.getId());
                    return Collections.nCopies((int) Math.max(1, regionBoost), rarity).stream();
                })
                .toList();
    }

    private boolean shouldSkipRarity(
            @NotNull Rarity rarity,
            double boostRate,
            Set<Rarity> boostedRarities,
            Player fisher
    ) {
        boolean isBoostFiltered = boostedRarities != null && boostRate == -1 && !boostedRarities.contains(rarity);
        boolean lacksPermission = rarity.getPermission() != null && !fisher.hasPermission(rarity.getPermission());
        return isBoostFiltered || lacksPermission;
    }


    public Fish getRandomWeightedFish(@NotNull List<Fish> fishList, double boostRate, List<Fish> boostedFish) {
        if (fishList.isEmpty()) return null;

        // Define a weight function that handles zero weights as 1
        ToDoubleFunction<Fish> weightFunction = fish -> {
            double weight = fish.getWeight();
            return weight == 0.0d ? 1.0 : weight;
        };

        Set<Fish> boostedSet = boostedFish == null ? Collections.emptySet() : new HashSet<>(boostedFish);

        return WeightedRandom.pick(fishList, weightFunction, boostRate, boostedSet, EvenMoreFish.getInstance().getRandom());
    }


    public Fish getFish(Rarity rarity, Location location, Player player, double boostRate, List<Fish> boostedFish, boolean doRequirementChecks, @Nullable Processor<?> processor, @Nullable CustomRod customRod) {
        if (rarity == null) {
            //possibly refactor to make rarity never null
            return null;
        }
        // will store all the fish that match the player's biome or don't discriminate biomes

        // Protection against /emf admin reload causing the plugin to be unable to get the rarity
        if (rarity.getOriginalFishList().isEmpty()) {
            rarity = getRandomWeightedRarity(player, 1, Collections.emptySet(), Set.copyOf(rarityMap.values()), customRod);
        }

        final List<Fish> customRodFish = customRod != null ? customRod.getAllowedFish() : Collections.emptyList();
        final World world = location == null ? null : location.getWorld();
        final RequirementContext context = new RequirementContext(world, location, player, null, null);
        final List<Fish> available = rarity.getFishList().stream()
                .filter(fish -> isFishAllowedByCustomRod(fish, customRodFish))
                .filter(fish -> isFishBoosted(fish, boostRate, boostedFish))
                .filter(fish -> isFishAllowedByProcessor(fish, processor))
                .filter(fish -> meetsRequirements(fish, doRequirementChecks, context))
                .toList();

        // if the config doesn't define any fish that can be fished in this biome.
        if (available.isEmpty()) {
            String biomeName = world != null ? world.getBiome(location).name() : "unknown biome";
            final Rarity finalRarity = rarity;
            EvenMoreFish.getInstance().getLogger().warning(() -> "No fish of rarity %s available at (x=%.1f, y=%.1f, z=%.1f) in biome %s. Custom rod restrictions: %b".formatted(
                    finalRarity.getId(),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    biomeName,
                    !customRodFish.isEmpty()));
            return null;
        }

        // checks whether weight calculations need doing for fish
        final Fish returningFish = getRandomWeightedFish(available, boostRate, boostedFish);

        return isFishAllowedOutsideCompetition(returningFish) ? returningFish : null;
    }

    private boolean isFishAllowedOutsideCompetition(Fish fish) {
        return Competition.isActive()
                || !MainConfig.getInstance().isFishCatchOnlyInCompetition()
                || (EvenMoreFish.getInstance().isRaritiesCompCheckExempt() && fish.isCompExemptFish());
    }

    private boolean isFishAllowedByCustomRod(Fish fish, @NotNull List<Fish> customRodFish) {
        // If no custom rod restrictions, all fish are allowed
        if (customRodFish.isEmpty()) {
            return true;
        }
        // Fish must be in the custom rod's allowed list
        return customRodFish.contains(fish);
    }

    private boolean isFishBoosted(Fish fish, double boostRate, List<Fish> boostedFish) {
        // If boostRate is -1 (disabled) or no boosted fish list, all fish are allowed
        if (boostRate == -1 || boostedFish == null) {
            return true;
        }
        // Fish must be in the boosted list
        return boostedFish.contains(fish);
    }

    private boolean isFishAllowedByProcessor(Fish fish, @Nullable Processor<?> processor) {
        // If no processor restriction, all fish are allowed
        if (processor == null) {
            return true;
        }
        // Fish must be allowed by the processor
        return processor.canUseFish(fish);
    }

    private boolean meetsRequirements(Fish fish, boolean doRequirementChecks, RequirementContext context) {
        // Skip requirement checks if disabled
        if (!doRequirementChecks) {
            return true;
        }
        // Check fish requirements against the context
        Requirement requirement = fish.getRequirement();
        return requirement.meetsRequirements(context);
    }

    public TreeMap<String, Rarity> getRarityMap() {
        return rarityMap;
    }

    // Loading things

    private void logLoadedItems() {
        int totalFish = rarityMap.values().stream()
                .mapToInt(rarity -> rarity.getOriginalFishList().size())
                .sum();

        EvenMoreFish.getInstance().getLogger().info(() -> "Loaded FishManager with %d Rarities and %d Fish.".formatted(rarityMap.size(), totalFish));
    }

    private void loadRarities() {

        rarityMap.clear();

        File raritiesFolder = new File(EvenMoreFish.getInstance().getDataFolder(), "rarities");
        if (!raritiesFolder.exists()) {
            loadDefaultFiles(raritiesFolder);
        }
        regenExampleFile(raritiesFolder);
        List<File> rarityFiles = FileUtil.getFilesInDirectory(raritiesFolder, true, true);

        if (rarityFiles.isEmpty()) {
            return;
        }

        rarityFiles.forEach(file -> {
            EvenMoreFish.getInstance().debug("Loading " + file.getName() + " rarity");
            Rarity rarity;
            try {
                rarity = new Rarity(file);
                // Skip invalid configs.
            } catch (InvalidConfigurationException exception) {
                return;
            }
            // Skip disabled files.
            if (rarity.isDisabled()) {
                return;
            }
            // Skip duplicate IDs
            String id = rarity.getId();
            if (rarityMap.containsKey(id)) {
                EvenMoreFish.getInstance().getLogger().warning("A rarity with the id: " + id + " already exists! Skipping.");
                return;
            }
            rarityMap.put(id, rarity);
        });
    }

    private void regenExampleFile(@NotNull File targetDirectory) {
        FileUtil.regenExampleFiles("rarities", targetDirectory);
    }


    private void loadDefaultFiles(@NotNull File targetDirectory) {
        EvenMoreFish.getInstance().getLogger().info("Loading default rarity configs from jar");

        FileUtil.loadFilesFromJarDirectory(
                "rarities",
                targetDirectory,
                file -> !file.startsWith("_") && file.endsWith(".yml"),
                false
        );
    }





}