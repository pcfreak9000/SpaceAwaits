package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Engine;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.world.ecs.chunk.ChunkReloadingSystem;
import de.pcfreak9000.spaceawaits.world.ecs.chunk.TickChunkSystem;
import de.pcfreak9000.spaceawaits.world.ecs.entity.MovingWorldEntitySystem;
import de.pcfreak9000.spaceawaits.world.light.LightCalculator;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystemBox2D;
import de.pcfreak9000.spaceawaits.world.render.RenderParallaxDecorator;
import de.pcfreak9000.spaceawaits.world.render.RenderChunkDecorator;
import de.pcfreak9000.spaceawaits.world.render.RenderEntityDecorator;
import de.pcfreak9000.spaceawaits.world.render.RenderSystem;

public class WorldManager {
    
    private final Engine ecsManager;
    private final WorldAccessor worldAccessor;
    
    public WorldManager() {
        this.ecsManager = new Engine();
        this.worldAccessor = new WorldAccessor(this);
        addDefaultECSSystems();
        SpaceAwaits.BUS.post(new WorldEvents.InitWorldManagerEvent(this.ecsManager));
    }
    
    public void updateAndRender(float delta) {
        ecsManager.update(delta);
    }
    
    //-> Make this configurable from the WorldGenerator and also have global systems
    private void addDefaultECSSystems() {//Create some indexed hook system thing instead?
        this.ecsManager.addSystem(new PlayerInputSystem());
        this.ecsManager.addSystem(new TickChunkSystem());
        PhysicsSystemBox2D phsys = new PhysicsSystemBox2D();
        this.ecsManager.addSystem(phsys);
        this.ecsManager.addSystem(new MovingWorldEntitySystem());
        this.ecsManager.addSystem(new CameraSystem());
        this.ecsManager.addSystem(new ChunkReloadingSystem(worldAccessor));
        RenderSystem rsys = new RenderSystem();
        rsys.registerRenderDecorator("entity", new RenderEntityDecorator());
        rsys.registerRenderDecorator("chunk", new RenderChunkDecorator());
        rsys.registerRenderDecorator("para", new RenderParallaxDecorator());
        this.ecsManager.addSystem(rsys);
        this.ecsManager.addSystem(new LightCalculator());
        //lightCalc.setProcessing(false);
        //this.ecsManager.addSystem(new PhysicsDebugRendererSystem(phsys));
    }
    
    public Engine getECSManager() {
        return this.ecsManager;
    }
    
    public WorldAccessor getWorldAccess() {
        return worldAccessor;
    }
}
