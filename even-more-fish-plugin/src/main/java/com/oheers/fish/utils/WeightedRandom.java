package com.oheers.fish.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.ToDoubleFunction;

public final class WeightedRandom {
    private WeightedRandom() {
        throw new UnsupportedOperationException();
    }

    public static <T> T pick(
            List<T> elements,
            ToDoubleFunction<T> weightFunction,
            @Nullable Random random
    ) {
        return pick(elements, weightFunction, 1.0, Collections.emptySet(), random);
    }

    public static <T> @Nullable T pick(
            @NotNull List<T> elements,
            ToDoubleFunction<T> weightFunction,
            double boostRate,
            @NotNull Set<T> boosted,
            @Nullable Random random
    ) {
        if (elements.isEmpty()) return null;

        Random rng = (random != null) ? random : new Random();
        double totalWeight = calcTotalWeight(elements, weightFunction, boostRate, boosted);

        double r = rng.nextDouble() * totalWeight;
        for (int i = 0; i < elements.size(); i++) {
            T element = elements.get(i);
            double weight = weightFunction.applyAsDouble(element);

            if (boostRate != -1 && boosted.contains(element)) {
                r -= weight * boostRate;
            } else {
                r -= weight;
            }

            if (r <= 0.0 || i == elements.size() - 1) {
                return element;
            }
        }

        return null;
    }

    private static <T> double calcTotalWeight(@NotNull List<T> elements, ToDoubleFunction<T> weightFunction, double boostRate,
                                       @NotNull Set<T> boosted) {
        double totalWeight = 0.0;
        for (T element : elements) {
            double weight = weightFunction.applyAsDouble(element);
            if (boostRate != -1 && boosted.contains(element)) {
                totalWeight += weight * boostRate;
            } else {
                totalWeight += weight;
            }
        }

        return totalWeight;
    }
}
