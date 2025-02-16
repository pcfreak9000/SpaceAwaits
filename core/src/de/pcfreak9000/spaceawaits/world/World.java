package de.pcfreak9000.spaceawaits.world;

import java.util.Random;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.RandomXS128;

import de.omnikryptec.event.EventBus;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.ecs.EngineImproved;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;

public abstract class World {
    
    public static final float STEPLENGTH_SECONDS = 1 / 60f;
    
    protected final EngineImproved ecsEngine;
    
    //Used for random item drops etc, not terrain gen etc
    private final RandomXS128 worldRandom;
    
    public World(WorldPrimer primer) {
        //initialize fields
        this.ecsEngine = new EngineImproved(STEPLENGTH_SECONDS);
        SpaceAwaits.BUS.register(this.ecsEngine.getEventBus());//Not too sure about this
        this.worldRandom = new RandomXS128(); //-> UtilSystem???????? RandomSystem?
        
    }
    
    public abstract void unloadWorld();
    
    public void update(float dt) {
        this.ecsEngine.update(dt);
    }
    
    protected Random getWorldRandom() {
        return worldRandom;
    }
    
    public void setPlayer(Player player) {
        ecsEngine.addEntity(player.getPlayerEntity());
    }
    
    //Could keep the Player instance here and just removePlayer()...
    public void removePlayer(Player player) {
        ecsEngine.removeEntity(player.getPlayerEntity());
    }
    
    public <T extends EntitySystem> T getSystem(Class<T> clazz) {
        return ecsEngine.getSystem(clazz);
    }
    
    public EventBus getWorldBus() {
        return this.ecsEngine.getEventBus();
    }
    
}
