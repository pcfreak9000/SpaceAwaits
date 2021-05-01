package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class UserData {
    
    private Tile tile;
    private Entity entity;
    private Object userDataRaw;
    
    public UserData() {
    }
    
    private void clear() {
        this.tile = null;
        this.entity = null;
        this.userDataRaw = null;
    }
    
    public UserData set(Object userdata) {
        clear();
        this.userDataRaw = userdata;
        if (userdata instanceof Tile) {
            this.tile = (Tile) userdata;
        } else if (userdata instanceof Entity) {
            this.entity = (Entity) userdata;
        }
        return this;
    }
    
    public Object getUserDataRaw() {
        return userDataRaw;
    }
    
    public boolean isTile() {
        return tile != null;
    }
    
    public boolean isEntity() {
        return entity != null;
    }
    
    public Tile getTile() {
        return tile;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
}
