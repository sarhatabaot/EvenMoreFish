package com.oheers.fish.messages;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;

public class EMFMessage {

    public static final MiniMessage MINIMESSAGE = MiniMessage.builder()
        .postProcessor(component -> component)
        .build();
    public static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
    public static final PlainTextComponentSerializer PLAINTEXT_SERIALIZER = PlainTextComponentSerializer.plainText();

    private final Map<String, Component> liveVariables = new LinkedHashMap<>();

    private Component message;
    private boolean canSilent = false;
    private OfflinePlayer relevantPlayer;
    protected boolean perPlayer = true;

    private EMFMessage(@Nullable Component message) {
        if (message == null) {
            this.message = Component.empty();
        } else {
            this.message = Component.empty().append(message);
        }
    }

    public static EMFMessage empty() {
        return new EMFMessage(null);
    }

    public static EMFMessage of(@NotNull Component component) {
        return new EMFMessage(component);
    }

    public static EMFMessage ofList(@NotNull List<Component> components) {
        Component finalComponent = Component.join(JoinConfiguration.newlines(), components);
        return new EMFMessage(finalComponent);
    }

    public static EMFMessage fromString(@NotNull String string) {
        return of(formatString(string));
    }

    public static EMFMessage fromStringList(@NotNull List<String> strings) {
        return ofList(strings.stream().map(EMFMessage::formatString).toList());
    }

    /**
     * Replaces all section symbols with ampersands so MiniMessage doesn't explode
     */
    private static Component formatString(@NotNull String message) {
        return MINIMESSAGE.deserialize(
            message.replace('§', '&')
        );
    }

    public void send(@NotNull CommandSender target) {
        if (isEmpty() || silentCheck()) {
            return;
        }

        Component originalMessage = this.message;

        if (perPlayer && target instanceof Player player) {
            setPlayer(player);
        }

        target.sendMessage(getComponentMessage());

        this.message = originalMessage;
    }

    public void sendActionBar(@NotNull CommandSender target) {
        if (isEmpty() || silentCheck()) {
            return;
        }

        Component originalMessage = this.message;

        if (perPlayer && target instanceof Player player) {
            setPlayer(player);
        }

        target.sendActionBar(getComponentMessage());

        this.message = originalMessage;
    }

    /**
     * @return The stored String in its raw form, with no colors or variables applied.
     */
    public @NotNull Component getComponentMessage() {
        formatVariables();
        formatPlaceholderAPI();
        return removeDefaultItalics(this.message);
    }

    public @NotNull List<Component> getComponentListMessage() {
        formatVariables();
        formatPlaceholderAPI();
        // Disgusting code incoming
        // Serialize the message into a MiniMessage String, split it by newlines, then deserialize each line into a Component
        String serialized = MINIMESSAGE.serialize(this.message);
        String[] split = serialized.split("<br>");
        return Arrays.stream(split).map(line -> {
            Component component = MINIMESSAGE.deserialize(line);
            return removeDefaultItalics(component);
        }).toList();
    }

    private @NotNull Component removeDefaultItalics(@NotNull Component component) {
        TextDecoration decoration = TextDecoration.ITALIC;
        TextDecoration.State oldState = component.decoration(decoration);
        if (oldState == TextDecoration.State.NOT_SET) {
            return component.decoration(decoration, TextDecoration.State.FALSE);
        }
        return component;
    }

    public @NotNull String getLegacyMessage() {
        return LEGACY_SERIALIZER.serialize(getComponentMessage());
    }

    public @NotNull List<String> getLegacyListMessage() {
        return Arrays.asList(getLegacyMessage().split("\n"));
    }

    public String getPlainTextMessage() {
        return PLAINTEXT_SERIALIZER.serialize(getComponentMessage());
    }

    /**
     * @return The formatted message as a plain text string list. All formatting will be removed.
     */
    public @NotNull List<String> getPlainTextListMessage() {
        return Arrays.asList(getPlainTextMessage().split("\n"));
    }

    public void formatPlaceholderAPI() {
        if (!isPAPIEnabled()) {
            return;
        }
        TextReplacementConfig trc = TextReplacementConfig.builder()
            .match(PlaceholderAPI.getPlaceholderPattern())
            .replacement((matchResult, builder) -> {
                String matched = matchResult.group();
                Component parsed = LEGACY_SERIALIZER.deserialize(
                    PlaceholderAPI.setPlaceholders(getRelevantPlayer(), matched)
                );
                return builder.append(parsed);
            })
            .build();
        this.message = this.message.replaceText(trc);
    }

    public void setMessage(@NotNull String message) {
        this.message = formatString(message);
    }

    public void setMessage(@NotNull Component message) {
        this.message = message;
    }

    public void setMessage(@NotNull EMFMessage message) {
        this.message = message.message;
    }

    private boolean isPAPIEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    /**
     * Formats all variables in {@link #liveVariables}
     */
    public void formatVariables() {
        TextReplacementConfig.Builder trc = TextReplacementConfig.builder();
        for (Map.Entry<String, Component> entry : liveVariables.entrySet()) {
            Component replaceComponent = entry.getValue();
            String placeholder =entry.getKey();
            trc.matchLiteral(placeholder).replacement(replaceComponent);
            this.message = this.message.replaceText(trc.build());
        }
    }

    public void setPerPlayer(boolean perPlayer) {
        this.perPlayer = perPlayer;
    }

    /**
     * Sends this message to the entire server.
     */
    public void broadcast() {
        send(Bukkit.getConsoleSender());
        Bukkit.getOnlinePlayers().forEach(this::send);
    }

    /**
     * Sends this message to the provided list of targets.
     * @param targets The targets of this message.
     */
    public void send(@NotNull List<CommandSender> targets) {
        targets.forEach(this::send);
    }

    /**
     * Sends this message to the entire server as an action bar.
     */
    public void broadcastActionBar() {
        Bukkit.getOnlinePlayers().forEach(this::sendActionBar);
    }

    /**
     * Sends this message to the provided list of targets as an action bar.
     * @param targets The targets of this message.
     */
    public void sendActionBar(@NotNull List<CommandSender> targets) {
        targets.forEach(this::sendActionBar);
    }

    /**
     * Adds the provided string to the end of this message.
     * @param message The string to append
     */
    public void appendString(@NotNull String message) {
        this.message = this.message.append(formatString(message));
    }

    /**
     * Adds the provided message to the end of this message.
     * @param message The message to append
     */
    public void appendMessage(@NotNull EMFMessage message) {
        message.formatVariables();
        this.message = this.message.append(message.message);
    }

    /**
     * Adds the provided component to the end of this message.
     * @param component The message to append
     */
    public void appendComponent(@NotNull Component component) {
        this.message = this.message.append(component);
    }

    /**
     * Adds the provided strings to the end of this message.
     * @param messages The strings to append
     */
    public void appendStringList(@NotNull List<String> messages) {
        appendString(String.join("\n", messages));
    }

    /**
     * Adds the provided messages to the end of this message.
     * @param messages The messages to append
     */
    public void appendMessageList(@NotNull List<EMFMessage> messages) {
        messages.forEach(this::appendMessage);
    }

    /**
     * Adds the provided components to the end of this message.
     * @param components The components to append
     */
    public void appendComponentList(@NotNull List<Component> components) {
        components.forEach(this::appendComponent);
    }

    /**
     * Adds the provided string to the start of this message.
     * @param message The string to prepend
     */
    public void prependString(@NotNull String message) {
        this.message = Component.empty().append(formatString(message)).append(this.message);
    }

    /**
     * Adds the provided message to the start of this message.
     * @param message The message to prepend
     */
    public void prependMessage(@NotNull EMFMessage message) {
        message.formatVariables();
        this.message = message.message.append(this.message);
    }

    /**
     * Adds the provided component to the start of this message.
     * @param component The component to prepend
     */
    public void prependComponent(@NotNull Component component) {
        this.message = component.append(this.message);
    }

    /**
     * Adds the provided strings to the start of this message.
     * @param messages The strings to prepend
     */
    public void prependStringList(@NotNull List<String> messages) {
        prependString(String.join("\n", messages));
    }

    /**
     * Adds the provided messages to the start of this message.
     * @param messages The messages to prepend
     */
    public void prependMessageList(@NotNull List<EMFMessage> messages) {
        messages.forEach(this::prependMessage);
    }

    /**
     * Adds the provided components to the start of this message.
     * @param components The components to prepend
     */
    public void prependComponentList(@NotNull List<Component> components) {
        components.forEach(this::prependComponent);
    }

    /**
     * @param canSilent Should this message support -s?
     */
    public void setCanSilent(boolean canSilent) {
        this.canSilent = canSilent;
    }

    /**
     * @return Does this message support -s?
     */
    public boolean isCanSilent() {
        return this.canSilent;
    }

    /**
     * @return Should this message be silent?
     */
    public boolean silentCheck() {
        return canSilent && getPlainTextMessage().endsWith(" -s");
    }

    /**
     * Sets the relevant player for this message.
     * @param player The relevant player.
     */
    public void setRelevantPlayer(@Nullable OfflinePlayer player) {
        this.relevantPlayer = player;
    }

    /**
     * @return This message's relevant player, or null if not available.
     */
    public @Nullable OfflinePlayer getRelevantPlayer() {
        return this.relevantPlayer;
    }

    /**
     * Adds a variable to be formatted when {@link #formatVariables()} is called.
     * @param variable The variable.
     * @param replacement The replacement for the variable.
     */
    public void setVariable(@NotNull final String variable, @NotNull final Object replacement) {
        if (replacement instanceof EMFMessage emfMessage) {
            emfMessage.formatVariables();
            this.liveVariables.put(variable, emfMessage.message);
        } else if (replacement instanceof Component component) {
            this.liveVariables.put(variable, component);
        } else {
            this.liveVariables.put(variable, formatString(String.valueOf(replacement)));
        }
    }

    /**
     * Adds a map of variables to be formatted when {@link #formatVariables()} is called.
     * @param variableMap The map of variables and their replacements.
     */
    public void setVariables(@Nullable Map<String, ?> variableMap) {
        if (variableMap == null || variableMap.isEmpty()) {
            return;
        }
        variableMap.forEach(this::setVariable);
    }

    /**
     * The player's name to replace the {player} variable. Also sets the relevantPlayer variable to this player.
     *
     * @param player The player.
     */
    public void setPlayer(@NotNull final OfflinePlayer player) {
        this.relevantPlayer = player;
        setVariable("{player}", Objects.requireNonNullElse(player.getName(), "N/A"));
    }

    /**
     * The fish's length to replace the {length} variable.
     *
     * @param length The length of the fish.
     */
    public void setLength(@NotNull final Object length) {
        setVariable("{length}", length);
    }

    /**
     * The rarity of the fish to replace the {rarity} variable.
     *
     * @param rarity The fish's rarity.
     */
    public void setRarity(@NotNull final Object rarity) {
        setVariable("{rarity}", rarity);
        setVariable("{rarity_colour}", "");
    }

    /**
     * Sets the fish name to replace the {fish} variable.
     *
     * @param fish The fish's name.
     */
    public void setFishCaught(@NotNull final Object fish) {
        setVariable("{fish}", fish);
    }

    /**
     * The price after a fish has been sold in /emf shop to replace the {sell-price} variable.
     *
     * @param sellPrice The sell price of the fish.
     */
    public void setSellPrice(@NotNull final Object sellPrice) {
        setVariable("{sell-price}", sellPrice);
    }

    /**
     * The amount of whatever, used multiple times throughout the plugin to replace the {amount} variable.
     *
     * @param amount The amount of x.
     */
    public void setAmount(@NotNull final Object amount) {
        setVariable("{amount}", amount);
    }

    /**
     * Sets the position for the {position} variable in the /emf top leaderboard.
     *
     * @param position The position.
     */
    public void setPosition(@NotNull final Object position) {
        setVariable("{position}", position);
    }

    /**
     * Sets the colour of the position for the {pos_colour} variable in the /emf top leaderboard.
     *
     * @param positionColour The position.
     */
    public void setPositionColour(@NotNull final Object positionColour) {
        setVariable("{pos_colour}", positionColour);
    }

    /**
     * Sets the formatted (Nh, Nm, Ns) time to replace the {time_formatted} variable.
     *
     * @param timeFormatted The formatted time.
     */
    public void setTimeFormatted(@NotNull final Object timeFormatted) {
        setVariable("{time_formatted}", timeFormatted);
    }

    /**
     * Sets the raw time (Nh:Nm:Ns) time to replace the {time_raw} variable.
     *
     * @param timeRaw The raw time.
     */
    public void setTimeRaw(@NotNull final Object timeRaw) {
        setVariable("{time_raw}", timeRaw);
    }

    /**
     * Sets the bait in the message to replace the {bait} variable.
     *
     * @param bait The name of the bait.
     */
    public void setBait(@NotNull final Object bait) {
        setVariable("{bait}", bait);
    }

    /**
     * Defines the theme of the bait to be used throughout the message to replace the {bait_theme} variable.
     *
     * @param baitTheme The bait colour theme.
     */
    public void setBaitTheme(@NotNull final Object baitTheme) {
        setVariable("{bait_theme}", baitTheme);
    }

    /**
     * Defines how many days should replace the {days} variable.
     *
     * @param days The number of days.
     */
    public void setDays(@NotNull final Object days) {
        setVariable("{days}", days);
    }

    /**
     * Defines how many hours should replace the {hours} variable.
     *
     * @param hours The number of hours.
     */
    public void setHours(@NotNull final Object hours) {
        setVariable("{hours}", hours);
    }

    /**
     * Defines how many minutes should replace the {minutes} variable.
     *
     * @param minutes The number of minutes.
     */
    public void setMinutes(@NotNull final Object minutes) {
        setVariable("{minutes}", minutes);
    }

    /**
     * Defines the result for the toggle MSG to replace the {toggle_msg} variable.
     *
     * @param toggleMSG The applicable toggle msg.
     */
    public void setToggleMSG(@NotNull final Object toggleMSG) {
        setVariable("{toggle_msg}", toggleMSG);
    }

    /**
     * Defines the result for the toggle material to replace the {toggle_icon} variable.
     *
     * @param toggleIcon The applicable toggle material.
     */
    public void setToggleIcon(@NotNull final Object toggleIcon) {
        setVariable("{toggle_icon}", toggleIcon);
    }

    /**
     * Defines which day should replace the {day} variable.
     *
     * @param day The day number.
     */
    public void setDay(@NotNull final Object day) {
        setVariable("{day}", day);
    }

    /**
     * Defines the name of the fish to be used, alternate to {fish}.
     *
     * @param name The name of the fish or user
     */
    public void setName(@NotNull final Object name) {
        setVariable("{name}", name);
    }

    /**
     * Defines the number of fish caught in the user's fish reports.
     *
     * @param numCaught The number of fish caught.
     */
    public void setNumCaught(@NotNull final Object numCaught) {
        setVariable("{num_caught}", numCaught);
    }

    /**
     * Defines the largest fish caught by the user in their fish reports.
     *
     * @param largestSize The largest size of the fish.
     */
    public void setLargestSize(@NotNull final Object largestSize) {
        setVariable("{largest_size}", largestSize);
    }

    /**
     * The first fish to be caught by the user in their fish reports.
     *
     * @param firstCaught The first fish caught.
     */
    public void setFirstCaught(@NotNull final Object firstCaught) {
        setVariable("{first_caught}", firstCaught);
    }

    /**
     * The time remaining for the fish to be unlocked.
     *
     * @param timeRemaining The time remaining.
     */
    public void setTimeRemaining(@NotNull final Object timeRemaining) {
        setVariable("{time_remaining}", timeRemaining);
    }

    /**
     * Sets the competition type, checking against the values for each type stored in messages.yml to replace the {type}
     * variable.
     *
     * @param typeString The competition type.
     */
    public void setCompetitionType(@NotNull final Object typeString) {
        setVariable("{type}", typeString);
    }

    /**
     * The amount of baits currently applied to the item.
     *
     * @param currentBaits The amount of baits.
     */
    public void setCurrentBaits(@NotNull final Object currentBaits) {
        setVariable("{current_baits}", currentBaits);
    }

    /**
     * The max amount of baits that can be applied to the item.
     *
     * @param maxBaits The max amount of baits.
     */
    public void setMaxBaits(@NotNull final Object maxBaits) {
        setVariable("{max_baits}", maxBaits);
    }

    public boolean isEmpty() {
        return PLAINTEXT_SERIALIZER.serialize(this.message).isEmpty();
    }

    public boolean containsString(@NotNull String string) {
        return PLAINTEXT_SERIALIZER.serialize(this.message).contains(string);
    }

}