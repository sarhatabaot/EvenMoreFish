package com.oheers.fish.utils;

import com.oheers.fish.config.MainConfig;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public final class NumberFormatUtils {

    private NumberFormatUtils() {
        throw new UnsupportedOperationException();
    }

    public static double roundDouble(final double value, final int places) {
        return BigDecimal.valueOf(value)
                .setScale(places, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static @NotNull Component formatDouble(@NotNull final String formatStr, final double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(MainConfig.getInstance().getDecimalLocale());
        DecimalFormat format = new DecimalFormat(formatStr, symbols);
        return Component.text(format.format(value));
    }

    public static float roundFloat(final float value, int places) {
        return BigDecimal.valueOf(value)
                .setScale(places, RoundingMode.HALF_UP)
                .floatValue();
    }

    public static String formatFloat(@NotNull final String formatStr, final float value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(MainConfig.getInstance().getDecimalLocale());
        DecimalFormat format = new DecimalFormat(formatStr, symbols);
        return format.format(value);
    }

    public static @Nullable Integer getInteger(@NotNull String intString) {
        try {
            return Integer.parseInt(intString);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}