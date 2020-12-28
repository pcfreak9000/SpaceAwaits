package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.WorldEvents;
import de.pcfreak9000.spaceawaits.tileworld.WorldRenderInfo;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;
import de.pcfreak9000.spaceawaits.tileworld.tile.TileWorld;

public class ParallaxSystem extends IteratingSystem {
    
    public ParallaxSystem() {
        super(Family.all(ParallaxComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    private final ComponentMapper<ParallaxComponent> parallaxMapper = ComponentMapper.getFor(ParallaxComponent.class);
    
    private TileWorld tileWorld;
    private WorldRenderInfo render;
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.tileWorld = svwe.getTileWorldNew();
        this.render = svwe.worldMgr.getRenderInfo();
    }
    
    @Override
    public void update(float deltaTime) {
        this.render.getSpriteBatch().begin();
        super.update(deltaTime);
        this.render.getSpriteBatch().end();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ParallaxComponent pc = this.parallaxMapper.get(entity);
        Vector3 positionState = this.render.getCamera().position;
        float xratio = positionState.x / (this.tileWorld.getWorldWidth() * Tile.TILE_SIZE);
        float yratio = positionState.y / (this.tileWorld.getWorldHeight() * Tile.TILE_SIZE);
        float possibleW = pc.sprite.getWidth() - this.render.getCamera().viewportWidth;
        float possibleH = pc.sprite.getHeight() - this.render.getCamera().viewportHeight;
        pc.sprite.setPosition(positionState.x - this.render.getCamera().viewportWidth / 2 - xratio * possibleW,
                positionState.y - this.render.getCamera().viewportHeight / 2 - yratio * possibleH);
        pc.sprite.draw(this.render.getSpriteBatch());
    }
    
}
