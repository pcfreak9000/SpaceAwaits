package de.pcfreak9000.spaceawaits.save;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectIntMap;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

public class ChunkDict implements INBTSerializable {
    
    private IntMap<String> intToId = new IntMap<>();
    private ObjectIntMap<String> idToInt = new ObjectIntMap<>();
    
    @NBTSerialize(key = "idc")
    private int idCounter = 0;
    
    public String getStringFrom(int id) {
        if (id == -1) {
            throw new IllegalArgumentException();//Maybe instead return the id of Tile.Nothing
        }
        return intToId.get(id);
    }
    
    public int getIdFor(String tileid) {
        int id = idToInt.get(tileid, -1);
        if (id == -1) {
            id = idCounter;
            idCounter++;
            idToInt.put(tileid, id);
            intToId.put(id, tileid);
        }
        return id;
    }
    
    public int getMax() {
        return idCounter;
    }
    
    @Override
    public void readNBT(NBTCompound tag) {
        for (String s : tag.keySet()) {
            int id = (int) tag.getIntegerSmart(s);
            intToId.put(id, s);
            idToInt.put(s, id);
        }
    }
    
    @Override
    public void writeNBT(NBTCompound comp) {
        for (String s : idToInt.keys()) {
            comp.putIntegerSmart(s, idToInt.get(s, 0));
        }
    }
    
}
