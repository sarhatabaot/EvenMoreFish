package com.oheers.fish.api.addons;

import org.jetbrains.annotations.NotNull;

public record AddonMetadata(@NotNull String name, @NotNull String version, @NotNull String author) {}

