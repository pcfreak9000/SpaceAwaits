package de.pcfreak9000.spaceawaits.world.tile;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

public class LiquidState implements IMetadata, NBTSerializable {
    
    private int lasttick;
    private float liquid;
    private float liquidNew;
    private boolean settled;
    
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
    
    public boolean isSettled() {
        return settled;
    }
    
    public void setSettled(boolean b) {
        this.settled = b;
    }
    
    @Override
    public void reset() {
        this.lasttick = -1;
        this.liquid = 0;
        this.liquidNew = 0;
        this.settled = false;
    }
    
    @Override
    public void readNBT(NBTTag tag) {
        NBTCompound comp = (NBTCompound) tag;
        this.lasttick = comp.getInt("lasttick");
        this.liquid = comp.getFloat("liquid");
        this.liquidNew = comp.getFloat("liquidNew");
        this.settled = comp.getByte("settled") == 1;
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTCompound comp = new NBTCompound();
        comp.putInt("lasttick", lasttick);
        comp.putFloat("liquid", liquid);
        comp.putFloat("liquidNew", liquidNew);
        comp.putByte("settled", settled ? (byte) 1 : (byte) 0);
        return comp;
    }
}
