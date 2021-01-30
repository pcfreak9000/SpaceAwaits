package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.ashley.core.Engine;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.ParallaxSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.RenderChunkSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.RenderEntitySystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.TickChunkSystem;
import de.pcfreak9000.spaceawaits.tileworld.light.LightCalculator;
import de.pcfreak9000.spaceawaits.tileworld.tile.WorldProvider;

public class WorldManager {
    
    private final Engine ecsManager;
    private final WorldRenderInfo worldRenderInfo;
    private final WorldAccessor worldAccessor;
    public LightCalculator lightCalc;//TODO lightcalc visibility
    private WorldProvider currentWorld;
    
    public WorldManager() {
        this.ecsManager = new Engine();
        this.worldRenderInfo = new WorldRenderInfo();
        this.worldAccessor = new WorldAccessor(this);
        addDefaultECSSystems();
        SpaceAwaits.BUS.post(new WorldEvents.InitWorldManagerEvent(this.ecsManager));
    }
    
    public void updateAndRender(float delta) {
        worldAccessor.unloadload();
        worldRenderInfo.applyViewport();
        ecsManager.update(delta);
    }
    
    private void addDefaultECSSystems() {//Create some indexed hook system thing instead?
        this.ecsManager.addSystem(new PlayerInputSystem());
        this.ecsManager.addSystem(new TickChunkSystem());
        this.ecsManager.addSystem(new PhysicsSystem());
        this.ecsManager.addSystem(new CameraSystem());
        this.ecsManager.addSystem(new ParallaxSystem());
        this.ecsManager.addSystem(new RenderChunkSystem());//TODO fix order of rendering and logic...
        this.ecsManager.addSystem(new RenderEntitySystem());
        this.ecsManager.addSystem(lightCalc = new LightCalculator());
    }
    
    public void setWorld(WorldProvider world) {
        if (world != this.currentWorld) {
            SpaceAwaits.BUS.post(new WorldEvents.SetWorldEvent(this, currentWorld, world));
            this.currentWorld = world;
            this.worldAccessor.setWorldProvider(this.currentWorld);
        }
    }
    
    public boolean hasCurrentWorld() {
        return this.currentWorld != null;
    }
    
    public Engine getECSManager() {
        return this.ecsManager;
    }
    
    public WorldAccessor getWorldAccess() {
        return worldAccessor;
    }
    
    public WorldRenderInfo getRenderInfo() {
        return worldRenderInfo;
    }
    
    public void dispose() {
        this.worldRenderInfo.dispose();
    }
}
