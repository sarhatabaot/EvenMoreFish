package com.oheers.fish.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class WeightedRandomTest {

    record TestItem(String name, double weight) {

        @NotNull
        @Override
            public String toString() {
                return name;
            }
        }

    @Test
    void testPick_withSimpleWeights() {
        List<TestItem> items = List.of(
                new TestItem("A", 1.0),
                new TestItem("B", 2.0),
                new TestItem("C", 3.0)
        );

        Random rng = new Random(42); // fixed seed
        TestItem result = WeightedRandom.pick(items, i -> i.weight, rng);

        assertNotNull(result);
        assertTrue(Set.of("A", "B", "C").contains(result.name));
    }

    @Test
    void testPick_withBoostedElement() {
        TestItem a = new TestItem("A", 1.0);
        TestItem b = new TestItem("B", 1.0);
        List<TestItem> items = List.of(a, b);
        Set<TestItem> boosted = Set.of(b);

        Random rng = new Random(0); // ensure b has a higher chance
        Map<String, AtomicInteger> count = new HashMap<>();
        count.put("A", new AtomicInteger(0));
        count.put("B", new AtomicInteger(0));

        for (int i = 0; i < 1000; i++) {
            TestItem result = WeightedRandom.pick(items, i2 -> i2.weight, 2.0, boosted, rng);
            count.get(result.name).incrementAndGet();
        }

        // B should win more often because of boost
        assertTrue(count.get("B").get() > count.get("A").get());
    }

    @Test
    void testPick_returnsNullForEmptyList() {
        List<TestItem> items = Collections.emptyList();
        TestItem result = WeightedRandom.pick(items, i -> i.weight, new Random());
        assertNull(result);
    }

    @Test
    void testPick_withZeroWeights() {
        List<TestItem> items = List.of(
                new TestItem("A", 0.0),
                new TestItem("B", 0.0),
                new TestItem("C", 0.0)
        );

        // Should still return something — the last fallback handles this
        TestItem result = WeightedRandom.pick(items, i -> i.weight, new Random(0));
        assertNotNull(result);
        assertTrue(Set.of("A", "B", "C").contains(result.name));
    }

    @Test
    void testPick_distributionAccuracy() {
        // With weights 1:2:3, we expect approx 1/6, 2/6, 3/6 frequency
        List<TestItem> items = List.of(
                new TestItem("A", 1.0),
                new TestItem("B", 2.0),
                new TestItem("C", 3.0)
        );

        Random rng = new Random(123);
        Map<String, AtomicInteger> resultMap = new HashMap<>();
        resultMap.put("A", new AtomicInteger());
        resultMap.put("B", new AtomicInteger());
        resultMap.put("C", new AtomicInteger());

        int runs = 6000;
        for (int i = 0; i < runs; i++) {
            TestItem result = WeightedRandom.pick(items, i2 -> i2.weight, rng);
            resultMap.get(result.name).incrementAndGet();
        }

        int a = resultMap.get("A").get();
        int b = resultMap.get("B").get();
        int c = resultMap.get("C").get();

        // Expect roughly 1000 : 2000 : 3000 distribution
        assertTrue(a < b && b < c, "Expected order A < B < C in selection frequency");
    }
}
