package com.oheers.fish.utils;

import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.EMFSingleMessage;
import com.oheers.fish.messages.abstracted.EMFMessage;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class TimeUtils {

    private TimeUtils() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull EMFMessage timeFormat(long timeLeft) {
        long hours = timeLeft / 3600;
        long minutes = (timeLeft % 3600) / 60;
        long seconds = timeLeft % 60;

        EMFSingleMessage formatted = EMFSingleMessage.empty();

        if (hours > 0) {
            appendTimeUnit(formatted, ConfigMessage.BAR_HOUR.getMessage(), hours);
        }

        if (minutes > 0) {
            appendTimeUnit(formatted, ConfigMessage.BAR_MINUTE.getMessage(), minutes);
        }

        if (seconds > 0 || (minutes == 0 && hours == 0)) {
            appendTimeUnit(formatted, ConfigMessage.BAR_SECOND.getMessage(), seconds);
        }

        if (!formatted.isEmpty()) {
            formatted.trim();
        }

        return formatted;
    }

    private static void appendTimeUnit(@NotNull EMFSingleMessage message, @NotNull EMFMessage template, long value) {
        template.setVariable("{value}", String.valueOf(value));
        message.appendMessage(template);
        message.appendComponent(Component.space());
    }

    public static @NotNull String timeRaw(long timeLeft) {
        StringBuilder returning = new StringBuilder();
        long hours = timeLeft / 3600;

        if (timeLeft >= 3600) {
            returning.append(hours).append(":");
        }

        if (timeLeft >= 60) {
            returning.append((timeLeft % 3600) / 60).append(":");
        }

        returning.append(timeLeft % 60);
        return returning.toString();
    }
}