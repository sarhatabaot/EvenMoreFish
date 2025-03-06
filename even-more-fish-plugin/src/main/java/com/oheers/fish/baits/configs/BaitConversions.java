package com.oheers.fish.baits.configs;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.config.ConfigBase;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class BaitConversions {

    public void performCheck() {
        File baitFile = new File(EvenMoreFish.getInstance().getDataFolder(), "baits.yml");
        if (!baitFile.exists() || !baitFile.isFile()) {
            return;
        }
        EvenMoreFish.getInstance().getLogger().info("Performing automatic conversion of bait configs");
        File baitsDir = getBaitsDirectory();
        if (!baitsDir.exists()) {
            baitsDir.mkdirs();
        }
        ConfigBase config = new ConfigBase(baitFile, EvenMoreFish.getInstance(), false);
        Section baitSection = config.getConfig().getSection("baits");
        if (baitSection == null) {
            finalizeConversion(config);
            return;
        }
        for (String baitKey : baitSection.getRoutesAsStrings(false)) {
            Section section = baitSection.getSection(baitKey);
            if (section != null) {
                convertSectionToFile(section);
            }
        }
        finalizeConversion(config);
    }

    private void finalizeConversion(@NotNull ConfigBase baitConfig) {
        // Rename the file to baits.yml.old
        File file = baitConfig.getFile();
        file.renameTo(new File(EvenMoreFish.getInstance().getDataFolder(), "baits.yml.old"));
        file.delete();

        EvenMoreFish.getInstance().getLogger().severe("Your bait configs have been automatically converted to the new format.");
    }

    /**
     * @return The 'baits' directory. This may not exist yet.
     */
    public File getBaitsDirectory() {
        return new File(EvenMoreFish.getInstance().getDataFolder(), "baits");
    }

    private void convertSectionToFile(@NotNull Section section) {
        String id = section.getNameAsString();
        if (id == null) {
            return;
        }
        File file = new File(EvenMoreFish.getInstance().getDataFolder(), "baits/" + id + ".yml");
        ConfigBase configBase = new ConfigBase(file, EvenMoreFish.getInstance(), false);
        YamlDocument config = configBase.getConfig();
        config.setAll(section.getRouteMappedValues(true));
        config.set("id", id);
        config.set("disabled", false);
        configBase.save();
    }

}
