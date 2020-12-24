package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.TileWorld;
import de.pcfreak9000.spaceawaits.tileworld.WorldEvents;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;

public class ParallaxSystem extends IteratingSystem {
    
    public ParallaxSystem() {
        super(Family.all(ParallaxComponent.class, RenderComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    private final ComponentMapper<RenderComponent> renderMapper = ComponentMapper.getFor(RenderComponent.class);
    private final ComponentMapper<ParallaxComponent> parallaxMapper = ComponentMapper.getFor(ParallaxComponent.class);
    
    private TileWorld tileWorld;
    private Camera cam;
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.tileWorld = svwe.getTileWorldNew();
        this.cam = svwe.worldMgr.getRenderer().getCamera();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        RenderComponent rc = this.renderMapper.get(entity);
        ParallaxComponent pc = this.parallaxMapper.get(entity);
        Vector3 positionState = this.cam.position;
        float xratio = positionState.x / (this.tileWorld.getWorldWidth() * Tile.TILE_SIZE);
        float yratio = positionState.y / (this.tileWorld.getWorldHeight() * Tile.TILE_SIZE);
        rc.sprite.setPosition(xratio * pc.xMov - positionState.x - 1920 * pc.aspect / 2,
                yratio * pc.yMov - positionState.y - 1920 / 2);
    }
    
}
