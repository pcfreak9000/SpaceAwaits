package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.serialize.INBTSerializable;
import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

@NBTSerialize(key = "spaceawaitsTransform")
public class TransformComponent implements Component, INBTSerializable {
    
    public final Vector2 position;
    public float rotation;
    
    //dont touch this
    public float originx, originy;
    
    public TransformComponent() {
        this.position = new Vector2();
    }
    
    @Override
    public void readNBT(NBTCompound comp) {
        this.position.set(comp.getFloat("x"), comp.getFloat("y"));
        if (comp.hasKey("r")) {
            this.rotation = comp.getFloat("r");
            this.originx = comp.getFloat("rx");
            this.originy = comp.getFloat("ry");
        }
    }
    
    @Override
    public void writeNBT(NBTCompound comp) {
        comp.putFloat("x", position.x);
        comp.putFloat("y", position.y);
        if (rotation != 0) {
            comp.putFloat("r", rotation);
            comp.putFloat("rx", originx);
            comp.putFloat("ry", originy);
        }
    }
}
