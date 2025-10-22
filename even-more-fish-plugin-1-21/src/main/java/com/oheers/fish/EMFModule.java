package com.oheers.fish;

public class EMFModule extends EvenMoreFish{
    @Override
    public void loadCommands() {

    }

    @Override
    public void enableCommands() {
        //nothing
    }

    @Override
    public void registerCommands() {
        //nothing, we register in onLoad()
    }

    @Override
    public void disableCommands() {
        //nothing
    }
}
