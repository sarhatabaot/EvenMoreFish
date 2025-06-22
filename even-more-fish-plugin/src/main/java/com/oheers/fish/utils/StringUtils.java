package com.oheers.fish.utils;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.messages.abstracted.EMFMessage;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException();
    }

    public static char getFirstChar(@NotNull String string, char defaultChar) {
        try {
            return string.toCharArray()[0];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return defaultChar;
        }
    }

    public static boolean isLegacyString(@NotNull String string) {
        String stripped = EMFMessage.MINIMESSAGE.stripTags(string);
        return string.equals(stripped);
    }

    public static @NotNull Component parsePlaceholderAPI(@NotNull Component component, @Nullable OfflinePlayer target) {
        if (!EvenMoreFish.getInstance().getDependencyManager().isUsingPAPI()) {
            return component;
        }

        TextReplacementConfig trc = TextReplacementConfig.builder()
                .match(PlaceholderAPI.getPlaceholderPattern())
                .replacement((matchResult, builder) -> {
                    String matched = matchResult.group();
                    Component parsed = EMFMessage.LEGACY_SERIALIZER.deserialize(
                            PlaceholderAPI.setPlaceholders(target, matched)
                    );
                    return builder.append(parsed);
                })
                .build();

        return component.replaceText(trc);
    }

    public static boolean componentContainsString(@NotNull Component component, @NotNull String string) {
        return EMFMessage.PLAINTEXT_SERIALIZER.serialize(component).contains(string);
    }

    public static @NotNull Component decorateIfAbsent(@NotNull Component component,
                                                      @NotNull TextDecoration decoration,
                                                      @NotNull TextDecoration.State state) {
        return component.decoration(decoration,
                component.decoration(decoration) == TextDecoration.State.NOT_SET ? state : component.decoration(decoration)
        );
    }

    public static @NotNull String getFormat(@NotNull String colour) {
        return isLegacyString(colour) ? colour + "{name}" : getMiniMessageFormat(colour);
    }

    private static String getMiniMessageFormat(@NotNull String colour) {
        int openingTagEnd = colour.indexOf(">");
        if (openingTagEnd == -1) {
            return colour + "{name}";
        }

        if (colour.contains("</")) {
            return colour.substring(0, openingTagEnd + 1) + "{name}" + colour.substring(openingTagEnd + 1);
        }

        return colour.substring(0, openingTagEnd + 1) + "{name}";
    }
}