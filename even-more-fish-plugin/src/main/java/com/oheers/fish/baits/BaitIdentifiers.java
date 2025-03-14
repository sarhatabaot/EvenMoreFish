package com.oheers.fish.baits;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.Locale;

public class BaitIdentifiers implements Translator {

    public static Component getBaitLine() {
        return Component.translatable("emf.bait-line");
    }

    private static BaitIdentifiers instance;

    public static void register() {
        if (instance == null) {
            instance = new BaitIdentifiers();
        }
        GlobalTranslator.translator().addSource(instance);
    }

    public static void unregister() {
        if (instance == null) {
            instance = new BaitIdentifiers();
        }
        GlobalTranslator.translator().removeSource(instance);
    }

    @Override
    public @NotNull Key name() {
        return Key.key("evenmorefish", "bait-identifiers");
    }

    @Override
    public @Nullable MessageFormat translate(@NotNull String key, @NotNull Locale locale) {
        return switch (key) {
            case "emf-bait-line" -> new MessageFormat("", locale);
            default -> null;
        };
    }

}
