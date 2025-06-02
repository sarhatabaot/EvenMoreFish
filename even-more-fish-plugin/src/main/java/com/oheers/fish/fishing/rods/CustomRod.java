package com.oheers.fish.fishing.rods;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.config.ConfigBase;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.FishManager;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.items.ItemFactory;
import com.oheers.fish.utils.Logging;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class CustomRod extends ConfigBase {

    private final List<Rarity> allowedRarities;
    private final List<Fish> allowedFish;
    private final ItemFactory factory;

    public CustomRod(@NotNull File file) throws InvalidConfigurationException {
        super(file, EvenMoreFish.getInstance(), false);
        performRequiredConfigChecks();
        this.allowedRarities = loadAllowedRarities();
        this.allowedFish = loadAllowedFish();
        this.factory = ItemFactory.itemFactory(getConfig());
    }

    // Current required config: id
    private void performRequiredConfigChecks() throws InvalidConfigurationException {
        if (getConfig().getString("id") == null) {
            Logging.warn("Custom Rod invalid: 'id' missing in " + getFileName());
            throw new InvalidConfigurationException("An ID has not been found in " + getFileName() + ". Please correct this.");
        }
    }

    // Loading things

    public List<Rarity> loadAllowedRarities() {
        return getConfig().getStringList("allowed-rarities").stream()
            .map(id -> FishManager.getInstance().getRarity(id))
            .filter(Objects::nonNull)
            .toList();
    }

    public List<Fish> loadAllowedFish() {
        Section section = getConfig().getSection("allowed-fish");
        if (section == null) {
            return List.of();
        }

        return section.getRoutesAsStrings(false).stream()
            .flatMap(rarityId -> {
                Rarity rarity = FishManager.getInstance().getRarity(rarityId);
                if (rarity == null) {
                    Logging.warn("Rarity '" + rarityId + "' not found for custom rod '" + getId() + "'.");
                    return Stream.empty();
                }
                return section.getStringList(rarityId)
                    .stream()
                    .map(rarity::getFish)
                    .filter(Objects::nonNull);
            })
            .toList();
    }

    // Config getters

    public @NotNull String getId() {
        return Objects.requireNonNull(getConfig().getString("id"));
    }

    public boolean isDisabled() {
        return getConfig().getBoolean("disabled");
    }

    public ItemFactory getFactory() {
        return this.factory;
    }

    public ItemStack create() {
        return factory.createItem();
    }

    public List<Rarity> getAllowedRarities() {
        return this.allowedRarities;
    }

    public List<Fish> getAllowedFish() {
        return this.allowedFish;
    }

}
