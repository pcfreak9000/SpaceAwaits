package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.world.World;

public class ParallaxSystem extends IteratingSystem {
    
    private World tileWorld;
    private Camera camera;
    
    public ParallaxSystem(World world, GameScreen renderer) {
        super(Family.all(ParallaxComponent.class, TransformComponent.class).get());
        this.tileWorld = world;
        this.camera = renderer.getCamera();
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ParallaxComponent pc = Components.PARALLAX.get(entity);
        Vector3 positionState = camera.position;
        float xratio = positionState.x / (this.tileWorld.getBounds().getWidth());
        float yratio = positionState.y / (this.tileWorld.getBounds().getHeight());
        float possibleW = pc.widthScroll;
        float possibleH = pc.heightScroll;
        Components.TRANSFORM.get(entity).position.set(positionState.x - pc.width / 2f - xratio * possibleW + pc.xOffset,
                positionState.y - pc.height / 2f - yratio * possibleH + pc.yOffset);
    }
    
}
