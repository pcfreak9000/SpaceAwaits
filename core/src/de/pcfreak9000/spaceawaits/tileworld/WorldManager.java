package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.ashley.core.Engine;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.ParallaxSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.RenderEntitySystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.RenderChunkSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.TickChunkSystem;
import de.pcfreak9000.spaceawaits.tileworld.light.LightCalculator;

public class WorldManager {
    
    private final Engine ecsManager;
    private final WorldRenderInfo worldRenderInfo;
    private final WorldLoader worldLoader;
    public LightCalculator lightCalc;//TODO lightcalc visibility
    private World currentWorld;
    
    public WorldManager() {
        this.ecsManager = new Engine();
        this.worldLoader = new WorldLoader(this);
        this.worldRenderInfo = new WorldRenderInfo();
        addDefaultECSSystems();
        SpaceAwaits.BUS.post(new WorldEvents.InitWorldManagerEvent(this.ecsManager));
    }
    
    public void updateAndRender(float delta) {
        worldLoader.loadChunks(delta);
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
    
    public void setWorld(World world) {
        if (world != this.currentWorld) {
            SpaceAwaits.BUS.post(new WorldEvents.SetWorldEvent(this, currentWorld, world));
            this.currentWorld = world;
            this.worldLoader.setWorld(this.currentWorld);
        }
    }
    
    public boolean hasCurrentWorld() {
        return this.currentWorld != null;
    }
    
    public Engine getECSManager() {
        return this.ecsManager;
    }
    
    public WorldLoader getLoader() {
        return worldLoader;
    }
    
    public WorldRenderInfo getRenderInfo() {
        return worldRenderInfo;
    }
    
    public void dispose() {
        this.worldRenderInfo.dispose();
    }
}
