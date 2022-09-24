package de.pcfreak9000.spaceawaits.world.tile;

import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

public class LiquidState implements ITileEntity {
    
    //This is useful, makes sure there isn't any small amounts of liquid laying around
    public static final float MIN_LIQUID = 0.001f;
    
    public LiquidState(float initialliquid) {
        setLiquid(initialliquid);
    }
    
    @NBTSerialize(key = "lt")
    private long lasttick;
    
    @NBTSerialize(key = "l")
    private float liquid;
    
    @NBTSerialize(key = "ln")
    public float liquidNew;
    
    public void addLiquid(float amount) {
        this.liquidNew += amount;
    }
    
    public float getLiquid() {
        return liquid;
    }
    
    public void setLiquid(float amount) {
        liquid = amount;
        liquidNew = amount;
    }
    
    public void updateLiquid(long tick) {
        if (tick != lasttick) {
            this.liquid = this.liquidNew;
            this.lasttick = tick;
        }
    }
    
    public boolean isEmpty() {
        return this.liquidNew <= MIN_LIQUID;
    }
    
}
