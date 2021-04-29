package de.pcfreak9000.spaceawaits.serialize;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTList;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.nbt.NBTType;

public class SerializableEntityList implements NBTSerializable {
    
    private Array<Entity> entities;
    
    public SerializableEntityList() {
        this.entities = new Array<>();
    }
    
    public Array<Entity> getEntities() {
        return entities;
    }
    
    public void addEntity(Entity e) {
        this.entities.add(e);
    }
    
    public void removeEntity(Entity e) {
        this.entities.removeValue(e, true);
    }
    
    @Override
    public void readNBT(NBTTag tag) {
        NBTList entities = (NBTList) tag;
        if (entities.getEntryType() != NBTType.Compound) {
            throw new IllegalArgumentException("Entity list is not a compound list");
        }
        for (NBTTag t : entities.getContent()) {
            Entity e = EntitySerializer.deserializeEntity((NBTCompound) t);
            if (e != null) {
                addEntity(e);
            }
        }
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTList entities = new NBTList(NBTType.Compound);
        for (Entity e : this.entities) {
            if (EntitySerializer.isSerializable(e)) {
                NBTCompound nbt = EntitySerializer.serializeEntity(e);
                entities.addCompound(nbt);
            }
        }
        return entities;
    }
}
