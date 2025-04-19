package com.oheers.fish.database.data;

import com.oheers.fish.fishing.items.Fish;
import com.oheers.fish.fishing.items.Rarity;
import org.jetbrains.annotations.NotNull;

public class UserFishRarityKey {
    private final int userId;
    private final String fishName;
    private final String fishRarity;

    public UserFishRarityKey(int userId, String fishName, String fishRarity) {
        this.userId = userId;
        this.fishName = fishName;
        this.fishRarity = fishRarity;
    }

    public static @NotNull UserFishRarityKey of(final int userId, final String fishName, final String fishRarity) {
        return new UserFishRarityKey(userId, fishName, fishRarity);
    }

    public static @NotNull UserFishRarityKey of(final int userId, final @NotNull Fish fish, final @NotNull Rarity rarity) {
        return new UserFishRarityKey(userId, fish.getName(), rarity.getId());
    }


    @Override
    public String toString() {
        return userId + "." +fishName + "." + fishRarity;
    }

    public String toStringDefault() {
        return "UserFishRarityKey{" +
                "userId='" + userId +'\'' +
                "fishName='" + fishName + '\'' +
                ", fishRarity='" + fishRarity + '\'' +
                '}';
    }
}