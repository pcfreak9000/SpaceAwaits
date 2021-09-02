package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.SerializableEntityList;
@Deprecated //not actually used
public class EntityContainer implements NBTSerializable {
    private SerializableEntityList entities;
    private Engine engine;
    
    public void addToEngine(Engine engine) {
        this.engine = engine;
        for (Entity e : entities.getEntities()) {
            engine.addEntity(e);
        }
    }
    
    public void removeFromEngine() {
        for (Entity e : entities.getEntities()) {
            engine.removeEntity(e);
        }
        this.engine = null;
    }
    
    public void addEntity(Entity e) {
        this.entities.addEntity(e);
        if (this.engine != null) {
            engine.addEntity(e);
        }
    }
    
    @Override
    public void readNBT(NBTTag tag) {
        this.entities.readNBT(tag);
    }
    
    @Override
    public NBTTag writeNBT() {
        return this.entities.writeNBT();
    }
}
