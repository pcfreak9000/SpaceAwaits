package de.pcfreak9000.spaceawaits.content.components;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

public class TreeStateComponent implements Component, NBTSerializable {
    
    public boolean loose;
    
    @Override
    public void readNBT(NBTTag tag) {
        loose = ((NBTTag.ByteEntry) tag).getByte() == (byte) 1;
    }
    
    @Override
    public NBTTag writeNBT() {
        return new NBTTag.ByteEntry(loose ? (byte) 1 : 0);
    }
    
}
