package com.oheers.fish.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NumberUtils {

    private NumberUtils() {
        throw new UnsupportedOperationException();
    }

    public static @Nullable Integer parseInteger(@NotNull String intString) {
        try {
            return Integer.parseInt(intString);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public static boolean isBetween(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean isPositive(int value) {
        return value > 0;
    }
}