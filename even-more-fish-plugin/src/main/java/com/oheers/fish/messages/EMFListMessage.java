package com.oheers.fish.messages;

import com.oheers.fish.FishUtils;
import com.oheers.fish.messages.abstracted.EMFMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextReplacementConfig;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EMFListMessage extends EMFMessage {

    private List<Component> message = new ArrayList<>();

    private EMFListMessage(@Nullable List<Component> message) {
        super();
        if (message != null) {
            this.message.addAll(message);
        }
    }

    @Override
    public EMFListMessage createCopy() {
        EMFListMessage copy = new EMFListMessage(List.copyOf(message));
        copy.liveVariables.putAll(this.liveVariables);
        return copy;
    }

    // Factory methods

    public static EMFListMessage empty() {
        return new EMFListMessage(null);
    }

    public static EMFListMessage of(@NotNull Component component) {
        return new EMFListMessage(List.of(component));
    }

    public static EMFListMessage ofList(@NotNull List<Component> components) {
        return new EMFListMessage(components);
    }

    public static EMFListMessage fromString(@NotNull String string) {
        return of(formatString(string));
    }

    public static EMFListMessage fromStringList(@NotNull List<String> strings) {
        return ofList(strings.stream().map(EMFListMessage::formatString).toList());
    }

    // Class methods

    @Override
    public void send(@NotNull Audience target) {
        target.sendMessage(getComponentMessage());
    }

    @Override
    public void sendActionBar(@NotNull Audience target) {
        target.sendActionBar(getComponentMessage());
    }

    /**
     * @return The stored components in their original form, with no variables applied.
     */
    public @NotNull List<Component> getRawMessage() {
        return this.message;
    }

    @Override
    public @NotNull Component getComponentMessage() {
        formatVariables();
        formatPlaceholderAPI();
        return Component.join(JoinConfiguration.newlines(), this.message);
    }

    @Override
    public @NotNull List<Component> getComponentListMessage() {
        formatVariables();
        formatPlaceholderAPI();
        return List.copyOf(this.message);
    }

    @Override
    public void formatPlaceholderAPI() {
        this.message = this.message.stream()
            .map(line -> FishUtils.parsePlaceholderAPI(line, relevantPlayer))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean isEmpty() {
        return message.isEmpty();
    }

    @Override
    public boolean containsString(@NotNull String string) {
        return this.message.stream().anyMatch(line -> FishUtils.componentContainsString(line, string));
    }

    @Override
    public void setMessage(@NotNull EMFMessage message) {
        this.message = new ArrayList<>(message.getComponentListMessage());
    }

    @Override
    public void appendString(@NotNull String string) {
        this.message.add(formatString(string));
    }

    @Override
    public void appendMessage(@NotNull EMFMessage message) {
        message.formatVariables();
        this.message.addAll(message.getComponentListMessage());
    }

    @Override
    public void appendComponent(@NotNull Component component) {
        this.message.add(component);
    }

    @Override
    public void prependString(@NotNull String string) {
        this.message.add(0, formatString(string));
    }

    @Override
    public void prependMessage(@NotNull EMFMessage message) {
        message.formatVariables();
        this.message.addAll(0, message.getComponentListMessage());
    }

    @Override
    public void prependComponent(@NotNull Component component) {
        this.message.add(0, component);
    }

    /**
     * Formats all variables in {@link #liveVariables}
     */
    // This feels awful, but I don't know if there's a better way to do it
    @Override
    public void formatVariables() {
        for (Map.Entry<String, List<Component>> entry : liveVariables.entrySet()) {
            String placeholder = entry.getKey();
            List<Component> replaceComponents = entry.getValue();
            for (Component replaceComponent : replaceComponents) {
                TextReplacementConfig trc = TextReplacementConfig.builder()
                    .matchLiteral(placeholder)
                    .replacement(replaceComponent)
                    .build();
                this.message = this.message.stream()
                    .map(line -> line.replaceText(trc))
                    .collect(Collectors.toCollection(ArrayList::new));
            }
        }
    }

}
