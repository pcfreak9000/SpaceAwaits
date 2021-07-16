package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;

import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class UserDataHelper {
    
    private Tile tile;
    private Entity entity;
    private UserData udCustom;
    private Object userDataRaw;
    private Fixture fixture;
    
    public UserDataHelper() {
    }
    
    public void clear() {
        this.tile = null;
        this.entity = null;
        this.userDataRaw = null;
        this.udCustom = null;
    }
    
    public UserDataHelper set(Object userdata, Fixture fix) {
        clear();
        this.fixture = fix;
        this.userDataRaw = userdata;
        if (userdata instanceof Tile) {
            this.tile = (Tile) userdata;
        } else if (userdata instanceof Entity) {
            this.entity = (Entity) userdata;
        } else if (userdata instanceof UserData) {
            this.udCustom = (UserData) userdata;
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
    
    public boolean isUDCustom() {
        return udCustom != null;
    }
    
    public Tile getTile() {
        return tile;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public UserData getUDCustom() {
        return udCustom;
    }
    
    public Fixture getFixture() {
        return fixture;
    }
    
}
