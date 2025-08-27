package com.oheers.fish.messages;

import com.oheers.fish.FishUtils;
import com.oheers.fish.messages.abstracted.EMFMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.firedev.messagelib.message.ComponentListMessage;
import uk.firedev.messagelib.message.ComponentMessage;
import uk.firedev.messagelib.message.ComponentSingleMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EMFListMessage extends EMFMessage {

    private ComponentListMessage underlying;

    private EMFListMessage(@NotNull ComponentListMessage message) {
        super();
        this.underlying = message;
    }

    @Override
    public EMFListMessage createCopy() {
        EMFListMessage newMessage = new EMFListMessage(underlying.createCopy());
        newMessage.perPlayer = this.perPlayer;
        newMessage.canSilent = this.canSilent;
        return newMessage;
    }

    @Override
    public @NotNull ComponentListMessage getUnderlying() {
        return underlying;
    }

    @Override
    public void setUnderlying(@NotNull ComponentMessage message) {
        if (message instanceof ComponentSingleMessage singleMessage) {
            this.underlying = singleMessage.toListMessage();
        } else if (message instanceof ComponentListMessage listMessage) {
            this.underlying = listMessage;
        } else {
            // Should never happen.
            return;
        }
    }

    // Factory methods

    public static EMFListMessage empty() {
        return new EMFListMessage(
            ComponentMessage.componentMessage(List.of())
        );
    }

    public static EMFListMessage of(@NotNull Component component) {
        return new EMFListMessage(
            ComponentMessage.componentMessage(List.of(component))
        );
    }

    public static EMFListMessage ofList(@NotNull List<Component> components) {
        return new EMFListMessage(
            ComponentMessage.componentMessage(components)
        );
    }

    public static EMFListMessage fromString(@NotNull String string) {
        return new EMFListMessage(
            ComponentMessage.componentMessage(List.of(string))
        );
    }

    public static EMFListMessage fromStringList(@NotNull List<String> strings) {
        return new EMFListMessage(
            ComponentMessage.componentMessage(strings)
        );
    }

    // Class methods

    /**
     * @return The stored components in their original form, with no variables applied.
     */
    public @NotNull List<Component> getRawMessage() {
        return this.underlying.get();
    }

    @Override
    public @NotNull Component getComponentMessage() {
        return getComponentMessage(null);
    }

    @Override
    public @NotNull Component getComponentMessage(@Nullable OfflinePlayer player) {
        return Component.join(JoinConfiguration.newlines(), getComponentListMessage(player));
    }

    @Override
    public @NotNull List<Component> getComponentListMessage() {
        return getComponentListMessage(null);
    }

    @Override
    public @NotNull List<Component> getComponentListMessage(@Nullable OfflinePlayer player) {
        return underlying.parsePlaceholderAPI(player)
            .replace("{player}", Optional.ofNullable(player).map(OfflinePlayer::getName).orElse("null"))
            .get();
    }

    @Override
    public void formatPlaceholderAPI() {
        this.underlying = this.underlying.parsePlaceholderAPI(relevantPlayer);
    }

    @Override
    public boolean isEmpty() {
        return underlying.isEmpty();
    }

    @Override
    public boolean containsString(@NotNull String string) {
        return underlying.toSingleMessages().stream().anyMatch(singleMessage -> singleMessage.containsString(string));
    }

}
