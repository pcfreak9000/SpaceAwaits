package de.pcfreak9000.spaceawaits.world.gen;

public class GeneratorSettings {
    
    private long seed;
    private boolean freshGame;
    
    public GeneratorSettings(long seed, boolean freshgame) {
        this.freshGame = freshgame;
    }
    
    public long getSeed() {
        return seed;
    }
    
    public boolean isFreshGame() {
        return freshGame;
    }
}
