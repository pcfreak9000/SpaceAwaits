package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Component;

import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;

@NBTSerialize(key = "spaceawaitsOnSolidGroundComponent")
public class OnSolidGroundComponent implements Component {
    
    public int solidGroundContacts;
    public int freemovementContacts;
    
    public boolean jumping = false;
    
    @NBTSerialize(key = "lcx")
    public float lastContactX;
    
    @NBTSerialize(key = "lcy")
    public float lastContactY;
    
    public boolean canMoveFreely() {
        return freemovementContacts > 0;
    }
    
    public boolean isOnSolidGround() {
        return solidGroundContacts > 0;
    }
}
