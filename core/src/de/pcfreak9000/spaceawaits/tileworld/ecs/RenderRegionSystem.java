package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.utils.IntSet;

import de.pcfreak9000.spaceawaits.tileworld.tile.Region;

public class RenderRegionSystem extends IteratingSystem implements EntityListener {
    
    private ComponentMapper<RegionComponent> tMapper = ComponentMapper.getFor(RegionComponent.class);
    
    private SpriteCache regionCache;
    private IntSet freeCacheIds;
    
    public RenderRegionSystem() {
        super(Family.all(RegionComponent.class).get());
        this.freeCacheIds = new IntSet();
        this.regionCache = new SpriteCache(100000, false);//Somewhere get information on how many regions will be cached at once so we can find out the required cache size
    }
    
    @Override
    public void entityAdded(Entity entity) {
        RegionComponent g = tMapper.get(entity);
        int cacheId;
        if (freeCacheIds.isEmpty()) {
            cacheId = createCache();
        } else {
            cacheId = freeCacheIds.first();
            freeCacheIds.remove(cacheId);
        }
        g.region.cacheId = cacheId;
    }
    
    private int createCache() {
        regionCache.beginCache();
        float[] empty = new float[5 * 6 * Region.REGION_TILE_SIZE * 2];//5 floats per vertex, 6 vertices per image, REGION_TILE_SIZE images per layer, 2 layers 
        regionCache.add(null, empty, 0, empty.length);
        return regionCache.endCache();
        //Dont allocate too many caches -> use some pooling or something (only regions that are loaded need a cache)
    }
    
    @Override
    public void entityRemoved(Entity entity) {
        RegionComponent g = tMapper.get(entity);
        freeCacheIds.add(g.region.cacheId);
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(getFamily(), this);
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntityListener(this);
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RegionComponent c = tMapper.get(entity);
        if(c.region.recacheTiles()) {
            regionCache.beginCache(c.region.cacheId);
            c.region.recacheTiles(regionCache);
            regionCache.endCache();
        }
    }
    
}