package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;

import de.pcfreak9000.spaceawaits.world.IGlobalLoader;
import de.pcfreak9000.spaceawaits.world.gen.IWorldGenerator;

public class WorldSystem extends EntitySystem implements Loadable {
    
    private IGlobalLoader loader;
    private IWorldGenerator gen;
    
    public WorldSystem(IGlobalLoader loader, IWorldGenerator gen) {
        this.loader = loader;
        this.gen = gen;
    }
    
    public void addEntity(Entity ent) {
        getEngine().addEntity(ent);
        loader.getEntities().addEntity(ent);
    }
    
    public void removeEntity(Entity ent) {
        getEngine().removeEntity(ent);
        loader.getEntities().removeEntity(ent);
    }
    
    @Override
    public void load() {
        loader.load();
        //((EngineImproved) getEngine()).getEventBus().post(new WorldEvents.WMNBTReadingEvent(loader.getData()));//Hmm        
        //world.getWorldBus().post(new WorldEvents.WMNBTWritingEvent(nbt));
        
        if (!loader.getData().getBooleanFromByteOrDefault("isGenerated", false)) {
            gen.generate(getEngine());
            loader.getData().putBooleanAsByte("isGenerated", true);
        }
        gen.onLoading(getEngine());
    }
    
    @Override
    public void unload() {
        loader.unload();
    }
    
}
