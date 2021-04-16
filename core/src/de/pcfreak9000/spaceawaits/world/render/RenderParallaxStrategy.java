package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector3;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.ecs.ParallaxComponent;

public class RenderParallaxStrategy extends AbstractRenderStrategy {
    
    public RenderParallaxStrategy() {
        super(Family.all(ParallaxComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    private final ComponentMapper<ParallaxComponent> parallaxMapper = ComponentMapper.getFor(ParallaxComponent.class);
    
    private WorldAccessor tileWorld;
    private WorldRenderer render;
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.tileWorld = svwe.worldMgr.getWorldAccess();
        this.render = SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer();
    }
    
    @Override
    public void begin() {
        SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().getSpriteBatch().begin();
    }
    
    @Override
    public void end() {
        SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer().getSpriteBatch().end();
    }
    
    @Override
    public void render(Entity entity, float deltaTime) {
        ParallaxComponent pc = this.parallaxMapper.get(entity);
        Vector3 positionState = this.render.getCamera().position;
        float xratio = positionState.x / (this.tileWorld.getWorldBounds().getWidth());
        float yratio = positionState.y / (this.tileWorld.getWorldBounds().getHeight());
        float possibleW = pc.sprite.getWidth() - this.render.getCamera().viewportWidth;
        float possibleH = pc.sprite.getHeight() - this.render.getCamera().viewportHeight;
        pc.sprite.setPosition(positionState.x - this.render.getCamera().viewportWidth / 2 - xratio * possibleW,
                positionState.y - this.render.getCamera().viewportHeight / 2 - yratio * possibleH);
        pc.action.act(pc.sprite);
        pc.sprite.draw(this.render.getSpriteBatch());
    }
    
}
