package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector3;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.ecs.ParallaxComponent;

public class RenderParallaxStrategy extends AbstractRenderStrategy {
    private static final ComponentMapper<ParallaxComponent> parallaxMapper = ComponentMapper
            .getFor(ParallaxComponent.class);
    
    private World tileWorld;
    private GameRenderer render;
    
    public RenderParallaxStrategy(World world) {
        super(Family.all(ParallaxComponent.class).get());
        this.tileWorld = world;
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.render = SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer();
    }
    
    @Override
    public void begin() {
        SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().getSpriteBatch().begin();
    }
    
    @Override
    public void end() {
        SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer().getSpriteBatch().end();
    }
    
    @Override
    public void render(Entity entity, float deltaTime) {
        ParallaxComponent pc = parallaxMapper.get(entity);
        Vector3 positionState = this.render.getView().getCamera().position;
        float xratio = positionState.x / (this.tileWorld.getBounds().getWidth());
        float yratio = positionState.y / (this.tileWorld.getBounds().getHeight());
        float possibleW = pc.sprite.getWidth() - this.render.getView().getCamera().viewportWidth;
        float possibleH = pc.sprite.getHeight() - this.render.getView().getCamera().viewportHeight;
        pc.sprite.setPosition(positionState.x - this.render.getView().getCamera().viewportWidth / 2 - xratio * possibleW,
                positionState.y - this.render.getView().getCamera().viewportHeight / 2 - yratio * possibleH);
        pc.action.act(pc.sprite);
        pc.sprite.draw(this.render.getSpriteBatch());
    }
    
}
