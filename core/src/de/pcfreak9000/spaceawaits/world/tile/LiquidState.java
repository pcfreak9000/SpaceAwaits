package de.pcfreak9000.spaceawaits.world.tile;

public class LiquidState implements IMetadata {
    
    private int lasttick;
    private float liquid;
    private float liquidNew;
    
    public void addLiquid(float amount) {
        this.liquidNew += amount;
    }
    
    public float getLiquid() {
        return liquid;
    }
    
    public void updateLiquid(int tick) {
        if (tick != lasttick) {
            this.liquid = this.liquidNew;
            this.lasttick = tick;
        }
    }
    
    public boolean isEmpty() {
        return this.liquidNew <= 0;
    }
    
    @Override
    public void reset() {
        this.lasttick = -1;
        this.liquid = 0;
        this.liquidNew = 0;
    }
}
