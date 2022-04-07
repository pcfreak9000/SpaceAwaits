package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderTextureComponent;

public class ParallaxSystem extends IteratingSystem {
    private static final ComponentMapper<ParallaxComponent> parallaxMapper = ComponentMapper
            .getFor(ParallaxComponent.class);
    private static final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    private static final ComponentMapper<RenderTextureComponent> renderMapper = ComponentMapper
            .getFor(RenderTextureComponent.class);
    
    private World tileWorld;
    private Camera camera;
    
    public ParallaxSystem(World world, GameRenderer renderer) {
        super(Family.all(ParallaxComponent.class, TransformComponent.class).get());
        this.tileWorld = world;
        this.camera = renderer.getCurrentView().getCamera();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ParallaxComponent pc = parallaxMapper.get(entity);
        Vector3 positionState = camera.position;
        float xratio = positionState.x / (this.tileWorld.getBounds().getWidth());
        float yratio = positionState.y / (this.tileWorld.getBounds().getHeight());
        RenderTextureComponent rc = renderMapper.get(entity);
        float possibleW = pc.widthScroll;
        float possibleH = pc.widthScroll;
        transformMapper.get(entity).position.set(positionState.x - rc.width / 2f - xratio * possibleW + pc.xOffset,
                positionState.y - rc.height / 2f - yratio * possibleH + pc.yOffset);
    }
    
}