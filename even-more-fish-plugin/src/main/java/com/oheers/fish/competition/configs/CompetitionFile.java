package com.oheers.fish.competition.configs;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.utils.FishUtils;
import com.oheers.fish.api.reward.Reward;
import com.oheers.fish.competition.CompetitionType;
import com.oheers.fish.config.ConfigBase;
import com.oheers.fish.fishing.items.FishManager;
import com.oheers.fish.fishing.items.Rarity;
import com.oheers.fish.messages.EMFSingleMessage;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class CompetitionFile extends ConfigBase {

    private static final Logger logger = EvenMoreFish.getInstance().getLogger();

    // We should never use the configUpdater for this.
    public CompetitionFile(@NotNull File file) throws InvalidConfigurationException {
        super(file, EvenMoreFish.getInstance(), false);
        CompetitionFileUpdates.update(this);
        performRequiredConfigChecks();
    }

    /**
     * Creates a new CompetitionFile with the provided values.
     * Used for the '/emf admin competition test' command.
     */
    public CompetitionFile(@NotNull String id, @NotNull CompetitionType type, int duration) {
        super();
        getConfig().set("id", id);
        getConfig().set("type", type.toString());
        getConfig().set("duration", duration);
    }

    // Current required config: id, type, times
    private void performRequiredConfigChecks() throws InvalidConfigurationException {
        if (getConfig().getString("id") == null) {
            logger.warning("Competition invalid: 'id' missing in " + getFileName());
            throw new InvalidConfigurationException("An ID has not been found in " + getFileName() + ". Please correct this.");
        }
        String type = getConfig().getString("type");
        if (type == null || CompetitionType.getType(type) == null) {
            logger.warning("Competition invalid: 'type' missing in " + getFileName());
            throw new InvalidConfigurationException("A type has not been found in " + getFileName() + ". Please correct this.");
        }
        if (getConfig().getInt("duration", -1) == -1) {
            logger.warning("Competition invalid: 'duration' missing in " + getFileName());
            throw new InvalidConfigurationException("A duration has not been found in " + getFileName() + ". Please correct this.");
        }
    }

    /**
     * @return The ID for this competition.
     */
    public @NotNull String getId() {
        return Objects.requireNonNull(getConfig().getString("id"));
    }

    /**
     * @return Should this competition be disabled?
     */
    public boolean isDisabled() {
        return getConfig().getBoolean("disabled");
    }

    /**
     * @return This competition's type.
     */
    public @NotNull CompetitionType getType() {
        return Objects.requireNonNull(CompetitionType.getType(getConfig().getString("type")));
    }

    /**
     * @return A list of times this competition should run at.
     */
    public @NotNull List<String> getTimes() {
        return getConfig().getStringList("times");
    }

    /**
     * @return A map of days and times to run this competition on.
     */
    public @NotNull Map<DayOfWeek, List<String>> getScheduledDays() {
        Section section = getConfig().getSection("days");
        Map<DayOfWeek, List<String>> dayMap = new HashMap<>();
        if (section == null) {
            return dayMap;
        }
        for (String dayStr : section.getRoutesAsStrings(false)) {
            DayOfWeek day = FishUtils.getDay(dayStr);
            if (day == null) {
                continue;
            }
            dayMap.put(day, section.getStringList(dayStr));
        }
        return dayMap;
    }

    /**
     * @return The duration of this competition (in minutes).
     */
    public int getDuration() {
        return Math.max(1, getConfig().getInt("duration"));
    }

    /**
     * @return The commands to execute when this competition starts.
     */
    public @NotNull List<String> getStartCommands() {
        String route = "start-commands";
        if (!getConfig().contains(route)) {
            return List.of();
        }
        if (getConfig().isList(route)) {
            return getConfig().getStringList(route);
        }
        return List.of(getConfig().getString(route));
    }

    /**
     * @return A list of days this competition should not run on.
     */
    public @NotNull List<DayOfWeek> getBlacklistedDays() {
        return getConfig().getStringList("blacklisted-days").stream()
                .map(FishUtils::getDay)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * @return A list of rarities that can be caught in this competition.
     */
    public @NotNull List<Rarity> getAllowedRarities() {
        return getConfig().getStringList("allowed-rarities").stream()
                .map(FishManager.getInstance()::getRarity)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * @return The number of fish needed for SPECIFIC_* competition types.
     */
    public int getNumberNeeded() {
        return Math.max(1, getConfig().getInt("number-needed", 0));
    }

    /**
     * @return Whether "fish caught" notifications only show for players holding fishing rods
     */
    public boolean shouldBroadcastOnlyRods() {
        return getConfig().getBoolean("broadcast-only-rods", true);
    }

    /**
     * @return The range of the "fish caught" notification, in blocks squared.
     */
    public int getBroadcastRange() {
        return getConfig().getInt("broadcast-range", -1);
    }

    public @NotNull List<String> getLeaderboardColours() {
        return getConfig().getStringList("leaderboard", List.of("<gold>{name}</gold>", "<yellow>{name}</yellow>", "<gray>{name}</gray>", "<gray>{name}</gray>", "<#888888>{name}</#888888>"));
    }

    public @NotNull List<Long> getAlertTimes() {
        List<String> times = getAlertTimesAsStrings();
        List<Long> finalTimes = new ArrayList<>();
        for (String time : times) {
            String[] split = time.split(":");
            if (split.length != 2) {
                logger.severe(time + " is not formatted correctly. Use MM:SS.");
                continue;
            }
            try {
                long seconds = Long.parseLong(split[1]);
                seconds += (Long.parseLong(split[0]) * 60);
                finalTimes.add(seconds);
            } catch (NumberFormatException exception) {
                logger.severe("Could not turn " + time + " into an alert time. If you need support, feel free to join the discord server: https://discord.gg/9fRbqWTnHS");
            }
        }
        return finalTimes;
    }

    /**
     * @return The times to broadcast the "time remaining" message, represented as Strings.
     */
    public @NotNull List<String> getAlertTimesAsStrings() {
        return getConfig().getStringList("alerts");
    }

    /**
     * @return The rewards to be given for winners of this competition.
     */
    public @NotNull Map<Integer, List<Reward>> getRewards() {
        Section section = getConfig().getSection("rewards");
        if (section == null) {
            return Map.of();
        }
        Map<Integer, List<Reward>> rewardMap = new HashMap<>();
        for (String positionStr : section.getRoutesAsStrings(false)) {
            Integer position;
            if (positionStr.equalsIgnoreCase("participation")) {
                position = -1;
            } else {
                position = FishUtils.getInteger(positionStr);
            }
            if (position == null) {
                continue;
            }
            List<Reward> rewards = section.getStringList(positionStr).stream()
                    .map(Reward::new)
                    .toList();
            rewardMap.put(position, rewards);
        }
        return rewardMap;
    }

    /**
     * @return The colour of this competition's bossbar.
     */
    public @NotNull BossBar.Color getBossbarColour() {
        String colour = getConfig().getString("bossbar-colour", "GREEN");
        try {
            return BossBar.Color.valueOf(colour.toUpperCase());
        } catch (IllegalArgumentException exception) {
            EvenMoreFish.getInstance().getLogger().warning(colour + " is not a valid bossbar colour. Defaulting to GREEN.");
            return BossBar.Color.GREEN;
        }
    }

    /**
     * @return Whether this competition should show its bossbar.
     */
    public boolean shouldShowBossbar() {
        return getConfig().getBoolean("show-bossbar", true);
    }

    /**
     * @return The prefix for this competition's bossbar.
     */
    public EMFSingleMessage getBossbarPrefix() {
        String prefix = getConfig().getString("bossbar-prefix", "<green><bold>Fishing Contest: ");
        return EMFSingleMessage.fromString(prefix);
    }

    /**
     * @return The amount of players required for this competition to start.
     */
    public int getPlayersNeeded() {
        return Math.max(1, getConfig().getInt("minimum-players", 5));
    }

    /**
     * @return The sound to play when this competition starts. Returns null if no sound should be played.
     */
    public @Nullable Sound getStartSound() {
        String soundString = getConfig().getString("start-sound", "NONE");
        if (soundString.equalsIgnoreCase("none")) {
            return null;
        }
        try {
            return Sound.valueOf(soundString.toUpperCase());
        } catch (IllegalArgumentException exception) {
            EvenMoreFish.getInstance().getLogger().warning(soundString + " is not a valid sound. Defaulting to NONE.");
            return null;
        }
    }

    /**
     * @return The worlds this competition is valid in.
     */
    public List<World> getRequiredWorlds() {
        return getConfig().getStringList("required-worlds").stream()
                .map(Bukkit::getWorld)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * @return Whether hunting is enabled.
     */
    public boolean isAllowHunting() {
        return getConfig().getBoolean("allow-hunting", false);
    }

    /**
     * @return Whether fishing is allowed.
     */
    public boolean isAllowFishing() {
        return getConfig().getBoolean("allow-fishing", true);
    }

}
