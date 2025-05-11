package com.oheers.fish.addons;

public enum DefaultAddons {

    J17("J17"),
    J21("J21");

    private final String identifier;
    private final String fullFileName;

    DefaultAddons(String identifier) {
        this.identifier = identifier;
        this.fullFileName = "EMF-Addons-" + identifier + ".addon";
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getFullFileName() {
        return fullFileName;
    }

}
