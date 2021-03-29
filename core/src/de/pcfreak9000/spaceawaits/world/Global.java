package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTList;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.nbt.NBTType;
import de.pcfreak9000.spaceawaits.serialize.EntitySerializer;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;

public class Global implements NBTSerializable {
    
    private AmbientLightProvider lightProvider;
    
    private Array<Entity> entities;
    private ImmutableArray<Entity> entitiesImmutable;
    
    public Global() {
        this.entities = new Array<>();
        this.entitiesImmutable = new ImmutableArray<>(this.entities);
        this.lightProvider = AmbientLightProvider.constant(Color.WHITE);
    }
    
    public ImmutableArray<Entity> getEntities() {
        return entitiesImmutable;
    }
    
    public AmbientLightProvider getLightProvider() {
        return lightProvider;
    }
    
    public void setLightProvider(AmbientLightProvider provider) {
        this.lightProvider = provider;
    }
    
    public void addEntity(Entity e) {
        this.entities.add(e);
    }
    
    public void removeEntity(Entity e) {
        this.entities.removeValue(e, true);
    }
    
    @Override
    public void readNBT(NBTTag compound) {
        NBTCompound nbtc = (NBTCompound) compound;
        NBTList entities = nbtc.getList("entities");
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
        NBTCompound nbtc = new NBTCompound();
        NBTList entities = new NBTList(NBTType.Compound);
        for (Entity e : this.entities) {
            if (EntitySerializer.isSerializable(e)) {
                NBTCompound nbt = EntitySerializer.serializeEntity(e);
                entities.add(nbt);
            }
        }
        nbtc.putList("entities", entities);
        //return null;
        return nbtc;
    }
}
