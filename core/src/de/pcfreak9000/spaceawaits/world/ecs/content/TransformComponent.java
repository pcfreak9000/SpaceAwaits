package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTag;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.serialize.NBTSerializable;

public class TransformComponent implements Component, NBTSerializable {
    
    static {
        GameRegistry.WORLD_COMPONENT_REGISTRY.register("spaceawaitsTransform", TransformComponent.class);
    }
    
    public final Vector2 position;
    
    public TransformComponent() {
        this.position = new Vector2();
    }
    
    @Override
    public void readNBT(NBTTag compound) {
        NBTCompound comp = (NBTCompound) compound;
        this.position.set(comp.getFloat("x"), comp.getFloat("y"));
    }
    
    @Override
    public NBTTag writeNBT() {
        NBTCompound comp = new NBTCompound();
        comp.putFloat("x", position.x);
        comp.putFloat("y", position.y);
        return comp;
    }
}
