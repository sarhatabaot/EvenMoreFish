package com.oheers.fish.placeholders;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.FishUtils;
import com.oheers.fish.competition.Competition;
import com.oheers.fish.competition.CompetitionType;
import com.oheers.fish.database.model.user.UserReport;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.abstracted.EMFMessage;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Handles all PlaceholderAPI expansions for EvenMoreFish.
 * <p>
 * Placeholders are processed in priority order:
 * 1. EXACT_MATCH (e.g. %emf_competition_active%)
 * 2. SPECIFIC_PATTERN (e.g. %emf_competition_place_player_1%)
 * 3. GENERAL_PATTERN (e.g. %emf_total_money_earned_<uuid>%)
 * 4. FALLBACK
 * </p>
 */
public class PlaceholderReceiver extends PlaceholderExpansion {

    private final EvenMoreFish plugin;
    private final List<HandlerDefinition> handlers = new ArrayList<>();


    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin The instance of our plugin.
     */
    public PlaceholderReceiver(EvenMoreFish plugin) {
        this.plugin = plugin;
        registerHandlers();
        sortHandlers();
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convenience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull String getAuthor() {
        return plugin.getPluginMeta().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public @NotNull String getIdentifier() {
        return "emf";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     * <p>
     * For convenience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    private void register(Predicate<String> matcher,
                          Priority priority,
                          BiFunction<Player, String, String> handler) {
        Objects.requireNonNull(matcher, "Matcher cannot be null");
        Objects.requireNonNull(priority, "Priority cannot be null");
        Objects.requireNonNull(handler, "Handler cannot be null");

        handlers.add(new HandlerDefinition(matcher, priority, handler));
    }

    private void sortHandlers() {
        handlers.sort(Comparator.comparingInt(def -> def.priority().ordinal()));
    }

    private void registerHandlers() {
        // Competition placeholders
        register(id -> id.startsWith("competition_place_size_"), Priority.SPECIFIC_PATTERN, this::handleCompetitionPlaceSize);
        register(id -> id.startsWith("competition_place_fish_"), Priority.SPECIFIC_PATTERN, this::handleCompetitionPlaceFish);
        register(id -> id.startsWith("competition_place_player_"), Priority.SPECIFIC_PATTERN, this::handleCompetitionPlacePlayer);

        // Total stats placeholders
        register(id -> id.startsWith("total_money_earned_"), Priority.GENERAL_PATTERN, this::handleTotalMoneyEarned);
        register(id -> id.startsWith("total_fish_sold_"), Priority.GENERAL_PATTERN, this::handleTotalFishSold);

        // Exact match placeholders
        register(id -> id.equalsIgnoreCase("competition_time_left"), Priority.EXACT_MATCH, this::handleCompetitionTimeLeft);
        register(id -> id.equalsIgnoreCase("competition_active"), Priority.EXACT_MATCH, this::handleCompetitionActive);
        register(id -> id.equalsIgnoreCase("custom_fishing_boolean"), Priority.EXACT_MATCH, this::handleCustomFishingBoolean);
        register(id -> id.equalsIgnoreCase("custom_fishing_status"), Priority.EXACT_MATCH, this::handleCustomFishingStatus);
        register(id -> id.equalsIgnoreCase("competition_type"), Priority.EXACT_MATCH, this::handleCompetitionType);
        register(id -> id.equalsIgnoreCase("competition_type_format"), Priority.EXACT_MATCH, this::handleCompetitionTypeFormat);
    }

    private String handleCompetitionPlacePlayer(Player player, String identifier) {
        Competition activeComp = Competition.getCurrentlyActive();
        if (activeComp == null) {
            return ConfigMessage.PLACEHOLDER_NO_COMPETITION_RUNNING.getMessage().getLegacyMessage();
        }

        // checking the leaderboard actually contains the value of place
        int place = Integer.parseInt(identifier.substring(25));
        if (!leaderboardContainsPlace(activeComp, place)) {
            return ConfigMessage.PLACEHOLDER_NO_PLAYER_IN_PLACE.getMessage().getLegacyMessage();
        }

        // getting "place" place in the competition
        UUID uuid;
        try {
            uuid = activeComp.getLeaderboard().getEntry(place).getPlayer();
        } catch (NullPointerException exception) {
            uuid = null;
        }
        if (uuid != null) {
            // To be in the leaderboard the player must have joined
            return Objects.requireNonNull(Bukkit.getOfflinePlayer(uuid)).getName();
        }
        return "";
    }

    private @NotNull String handleCompetitionTypeFormat(Player player, String identifier) {
        Competition activeComp = Competition.getCurrentlyActive();
        if (activeComp == null) {
            return ConfigMessage.PLACEHOLDER_NO_COMPETITION_RUNNING.getMessage().getLegacyMessage();
        }

        CompetitionType competitionType = activeComp.getCompetitionType();
        return switch (competitionType) {
            case LARGEST_FISH -> ConfigMessage.COMPETITION_TYPE_LARGEST.getMessage().getLegacyMessage();
            case LARGEST_TOTAL -> ConfigMessage.COMPETITION_TYPE_LARGEST_TOTAL.getMessage().getLegacyMessage();
            case MOST_FISH -> ConfigMessage.COMPETITION_TYPE_MOST.getMessage().getLegacyMessage();
            case SPECIFIC_FISH -> ConfigMessage.COMPETITION_TYPE_SPECIFIC.getMessage().getLegacyMessage();
            case SPECIFIC_RARITY -> ConfigMessage.COMPETITION_TYPE_SPECIFIC_RARITY.getMessage().getLegacyMessage();
            case SHORTEST_FISH -> ConfigMessage.COMPETITION_TYPE_SHORTEST.getMessage().getLegacyMessage();
            case SHORTEST_TOTAL -> ConfigMessage.COMPETITION_TYPE_SHORTEST_TOTAL.getMessage().getLegacyMessage();
            default -> "";
        };
    }

    private String handleCompetitionType(Player player, String identifier) {
        Competition activeComp = Competition.getCurrentlyActive();
        return activeComp != null
                ? activeComp.getCompetitionType().name()
                : ConfigMessage.PLACEHOLDER_NO_COMPETITION_RUNNING.getMessage().getLegacyMessage();
    }

    /* Competition Placeholder Handlers */
    private String handleCompetitionPlaceSize(Player player, String identifier) {
        Competition activeComp = Competition.getCurrentlyActive();
        if (activeComp == null) {
            return ConfigMessage.PLACEHOLDER_NO_COMPETITION_RUNNING_SIZE.getMessage().getLegacyMessage();
        }
        if (!(activeComp.getCompetitionType() == CompetitionType.LARGEST_FISH ||
                activeComp.getCompetitionType() == CompetitionType.LARGEST_TOTAL)) {
            return ConfigMessage.PLACEHOLDER_SIZE_DURING_MOST_FISH.getMessage().getLegacyMessage();
        }

        int place = Integer.parseInt(identifier.substring(23));
        if (!leaderboardContainsPlace(activeComp, place)) {
            return ConfigMessage.PLACEHOLDER_NO_SIZE_IN_PLACE.getMessage().getLegacyMessage();
        }

        float value;
        try {
            value = activeComp.getLeaderboard().getEntry(place).getValue();
        } catch (NullPointerException exception) {
            value = -1;
        }

        return value != -1.0f ? Double.toString(FishUtils.roundDouble(value, 1)) : "";
    }

    private String handleCompetitionPlaceFish(Player player, String identifier) {
        Competition activeComp = Competition.getCurrentlyActive();
        if (activeComp == null) {
            return ConfigMessage.PLACEHOLDER_NO_COMPETITION_RUNNING_FISH.getMessage().getLegacyMessage();
        }

        int place = Integer.parseInt(identifier.substring(23));

        if (activeComp.getCompetitionType() == CompetitionType.LARGEST_FISH) {
            if (!leaderboardContainsPlace(activeComp, place)) {
                return ConfigMessage.PLACEHOLDER_NO_FISH_IN_PLACE.getMessage().getLegacyMessage();
            }

            Fish fish = getFishFromLeaderboard(activeComp, place);
            if (fish != null) {
                return formatFishMessage(fish);
            }
        } else {
            float value = getValueFromLeaderboard(activeComp, place);
            if (value == -1) {
                return ConfigMessage.PLACEHOLDER_NO_FISH_IN_PLACE.getMessage().getLegacyMessage();
            }
            return formatMostFishMessage((int) value);
        }
        return "";
    }

    /* Stats Placeholder Handlers */
    private @Nullable String handleTotalMoneyEarned(Player player, @NotNull String identifier) {
        try {
            final UUID uuid = UUID.fromString(identifier.split("total_money_earned_")[1]);

            final UserReport userReport = EvenMoreFish.getInstance().getUserReportDataManager().get(String.valueOf(uuid));
            return userReport != null ? String.format("%.2f", userReport.getMoneyEarned()) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private @Nullable String handleTotalFishSold(Player player, @NotNull String identifier) {
        try {
            final UUID uuid = UUID.fromString(identifier.split("total_fish_sold_")[1]);
            final UserReport userReport = EvenMoreFish.getInstance().getUserReportDataManager().get(String.valueOf(uuid));
            return userReport != null ? String.valueOf(userReport.getFishSold()) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /* Simple Placeholder Handlers */
    private @NotNull String handleCompetitionTimeLeft(Player player, String identifier) {
        return Competition.getNextCompetitionMessage().getLegacyMessage();
    }

    private @NotNull String handleCompetitionActive(Player player, String identifier) {
        return Boolean.toString(Competition.isActive());
    }

    private @NotNull String handleCustomFishingBoolean(Player player, String identifier) {
        return Boolean.toString(!plugin.isCustomFishingDisabled(player));
    }

    private @NotNull String handleCustomFishingStatus(Player player, String identifier) {
        return plugin.isCustomFishingDisabled(player)
                ? ConfigMessage.CUSTOM_FISHING_DISABLED.getMessage().getLegacyMessage()
                : ConfigMessage.CUSTOM_FISHING_ENABLED.getMessage().getLegacyMessage();
    }

    /* Helper Methods */
    private @Nullable Fish getFishFromLeaderboard(Competition competition, int place) {
        try {
            return competition.getLeaderboard().getEntry(place).getFish();
        } catch (NullPointerException e) {
            return null;
        }
    }

    private float getValueFromLeaderboard(Competition competition, int place) {
        try {
            return competition.getLeaderboard().getEntry(place).getValue();
        } catch (NullPointerException e) {
            return -1;
        }
    }

    private @NotNull String formatFishMessage(@NotNull Fish fish) {
        EMFMessage message = fish.getLength() == -1
                ? ConfigMessage.PLACEHOLDER_FISH_LENGTHLESS_FORMAT.getMessage()
                : ConfigMessage.PLACEHOLDER_FISH_FORMAT.getMessage();

        message.setLength(Float.toString(fish.getLength()));
        message.setFishCaught(fish.getDisplayName());
        message.setRarity(fish.getRarity().getDisplayName());
        return message.getLegacyMessage();
    }

    private @NotNull String formatMostFishMessage(int amount) {
        EMFMessage message = ConfigMessage.PLACEHOLDER_FISH_MOST_FORMAT.getMessage();
        message.setAmount(amount);
        return message.getLegacyMessage();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";

        for (HandlerDefinition def : handlers) {
            if (def.matcher().test(identifier)) {
                return def.handler().apply(player, identifier);
            }
        }
        return null;
    }

    private record HandlerDefinition(
            Predicate<String> matcher,
            Priority priority,
            BiFunction<Player, String, String> handler
    ) {}

    private boolean leaderboardContainsPlace(@NotNull Competition competition, int place) {
        return competition.getLeaderboardSize() >= place;
    }
}