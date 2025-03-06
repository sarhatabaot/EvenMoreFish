package com.oheers.fish.config;

import com.oheers.fish.EvenMoreFish;

import java.util.List;

public class BaitFile extends ConfigBase {

    private static BaitFile instance = null;

    public BaitFile() {
        super("baits.yml", "baits.yml", EvenMoreFish.getInstance(), false);
        instance = this;
    }
    
    public static BaitFile getInstance() {
        return instance;
    }

}
