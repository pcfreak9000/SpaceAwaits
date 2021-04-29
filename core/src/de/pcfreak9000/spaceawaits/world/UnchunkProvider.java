package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.serialize.SerializableEntityList;
import de.pcfreak9000.spaceawaits.world.gen.IUnchunkGenerator;

public class UnchunkProvider implements IUnchunkProvider {
    
    private SerializableEntityList data;
    
    private World world;
    private IUnchunkGenerator unchunkGen;
    
    private IWorldSave save;
    
    public UnchunkProvider(World world, IUnchunkGenerator gen) {
        this.world = world;
        this.unchunkGen = gen;
    }
    
    public void setSave(IWorldSave save) {
        this.save = save;
    }
    
    public void load() {
        if (data != null) {
            return;
        }
        data = new SerializableEntityList();
        if (save.hasGlobal()) {
            NBTCompound nbt = save.readGlobal();
            data.readNBT(nbt.get("entities"));
            unchunkGen.regenerateUnchunk(data, world);
        } else {
            unchunkGen.generateUnchunk(data, world);
        }
    }
    
    @Override
    public SerializableEntityList get() {
        return data;
    }
    
    public void save() {
        if (data != null) {
            NBTCompound nbt = new NBTCompound();
            nbt.put("entities", data.writeNBT());
            save.writeGlobal(nbt);
        }
    }
    
    @Override
    public void unload() {
        save();
        data = null;
    }
    
}
