package com.oheers.fish.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.Rarity;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class FishArgument implements CustomArgumentType.Converted<Fish, String> {
    private static final DynamicCommandExceptionType UNKNOWN_FISH = new DynamicCommandExceptionType(
            obj -> MessageComponentSerializer.message().serialize(Component.text(obj + " is not a valid fish!"))
    );

    private static final DynamicCommandExceptionType NO_RARITY = new DynamicCommandExceptionType(
            obj -> MessageComponentSerializer.message().serialize(Component.text("Missing Rarity argument before Fish!"))
    );

    @Override
    public Fish convert(String nativeType) throws CommandSyntaxException {
        throw new UnsupportedOperationException("Cannot convert FishArgument without a Rarity context in Brigadier.");
    }

    @NotNull
    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

    @NotNull
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        Rarity rarity = context.getArgument("rarity", Rarity.class);
        if (rarity == null) {
            return builder.buildFuture();
        }

        rarity.getOriginalFishList().stream()
                .map(fish -> fish.getName().replace(" ", "_"))
                .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }
}
