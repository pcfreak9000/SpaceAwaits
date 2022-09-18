package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.SerializableEntityList;
import de.pcfreak9000.spaceawaits.world.gen.IWorldGenerator;

public class UnchunkProvider implements IUnchunkProvider {
    
    private SerializableEntityList data;
    private NBTCompound nbt = new NBTCompound();
    
    private World world;
    private IWorldGenerator worldGen;
    
    private IWorldSave save;
    
    public UnchunkProvider(IWorldSave save, World world, IWorldGenerator worldGenerator) {
        this.world = world;
        this.worldGen = worldGenerator;
        this.save = save;
    }
    
    public void load() {//Split this up in generation and loading?? this isn't so big, so this can stay for now
        if (data != null) {
            return;
        }
        data = new SerializableEntityList();
        if (save.hasGlobal()) {
            NBTCompound nbt = save.readGlobal();
            data.readNBT(nbt.getCompound("entities"));
            this.nbt = nbt.getCompound("dat");
        } else {
            worldGen.generate(world);
        }
        worldGen.onLoading(world);
        world.getWorldBus().post(new WorldEvents.WMNBTReadingEvent(nbt));
    }
    
    @Override
    public SerializableEntityList get() {
        return data;
    }
    
    public void save() {
        world.getWorldBus().post(new WorldEvents.WMNBTWritingEvent(nbt));
        if (data != null) {
            NBTCompound nbt = new NBTCompound();
            nbt.put("entities", INBTSerializable.writeNBT(data));
            nbt.put("dat", this.nbt);
            save.writeGlobal(nbt);
        }
    }
    
    @Override
    public void unload() {
        save();
        data = null;
    }
    
}
