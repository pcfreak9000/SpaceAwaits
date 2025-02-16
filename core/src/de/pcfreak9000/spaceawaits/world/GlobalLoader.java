package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.save.WorldSave;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.SerializableEntityList;

public class GlobalLoader implements IGlobalLoader {
    
    private SerializableEntityList data;
    private NBTCompound nbt;
    
    private WorldSave save;
    
    public GlobalLoader(WorldSave save) {
        this.save = save;
    }
    
    @Override
    public void load() {//Split this up in generation and loading?? this isn't so big, so this can stay for now
        if (data != null) {
            return;
        }
        data = new SerializableEntityList();
        if (save.hasGlobal()) {
            NBTCompound nbt = save.readGlobal();
            data.readNBT(nbt.getCompound("entities"));
            this.nbt = nbt.getCompound("data");
        } else {
            this.nbt = new NBTCompound();
        }
    }
    
    @Override
    public SerializableEntityList getEntities() {
        return data;
    }
    
    @Override
    public NBTCompound getData() {
        return nbt;
    }
    
    @Override
    public void save() {
        if (data != null) {
            NBTCompound nbt = new NBTCompound();
            nbt.put("entities", INBTSerializable.writeNBT(data));
            nbt.put("data", this.nbt);
            save.writeGlobal(nbt);
        }
    }
    
    @Override
    public void unload() {
        save();
        data = null;
        nbt = null;
    }
    
}
