package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.WorldRenderer;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class ParallaxSystem extends IteratingSystem {
    
    public ParallaxSystem() {
        super(Family.all(ParallaxComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    private final ComponentMapper<ParallaxComponent> parallaxMapper = ComponentMapper.getFor(ParallaxComponent.class);
    
    private WorldAccessor tileWorld;
    private WorldRenderer render;
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.tileWorld = svwe.worldMgr.getWorldAccess();
        this.render = SpaceAwaits.getSpaceAwaits().worldRenderer;
    }
    
    @Override
    public void update(float deltaTime) {
        SpaceAwaits.getSpaceAwaits().worldRenderer.getSpriteBatch().begin();
        super.update(deltaTime);
        SpaceAwaits.getSpaceAwaits().worldRenderer.getSpriteBatch().end();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ParallaxComponent pc = this.parallaxMapper.get(entity);
        Vector3 positionState = this.render.getCamera().position;
        float xratio = positionState.x / (this.tileWorld.getWorldBounds().getWidth() * Tile.TILE_SIZE);
        float yratio = positionState.y / (this.tileWorld.getWorldBounds().getHeight() * Tile.TILE_SIZE);
        float possibleW = pc.sprite.getWidth() - this.render.getCamera().viewportWidth;
        float possibleH = pc.sprite.getHeight() - this.render.getCamera().viewportHeight;
        pc.sprite.setPosition(positionState.x - this.render.getCamera().viewportWidth / 2 - xratio * possibleW,
                positionState.y - this.render.getCamera().viewportHeight / 2 - yratio * possibleH);
        pc.action.act(pc.sprite);
        pc.sprite.draw(this.render.getSpriteBatch());
    }
    
}
