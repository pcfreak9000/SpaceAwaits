package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;

import de.pcfreak9000.spaceawaits.world.IGlobalLoader;
import de.pcfreak9000.spaceawaits.world.IWorldProperties;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.WorldUtil;
import de.pcfreak9000.spaceawaits.world.gen.IWorldGenerator;
import de.pcfreak9000.spaceawaits.world.light.AmbientLightProvider;

public class WorldSystem extends EntitySystem implements Loadable {
    
    private final WorldBounds bounds;
    
    private IGlobalLoader loader;
    private IWorldGenerator gen;
    
    private IWorldProperties props;
    private AmbientLightProvider alp;
    
    public WorldSystem(IGlobalLoader loader, IWorldGenerator gen, WorldBounds bounds, IWorldProperties props,
            AmbientLightProvider alp) {
        this.loader = loader;
        this.gen = gen;
        this.bounds = bounds;
        this.props = props;
        this.alp = alp;
    }
    
    public IWorldProperties getWorldProperties() {
        return props;
    }
    
    public AmbientLightProvider getAmbientLightProvider() {
        return alp;
    }
    
    public WorldBounds getBounds() {
        return bounds;
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
        if (props.autoWorldBorders()) {
            WorldUtil.createWorldBorders(getEngine(), getBounds().getWidth(), getBounds().getHeight());
        }
    }
    
    @Override
    public void unload() {
        loader.unload();
    }
    
}
