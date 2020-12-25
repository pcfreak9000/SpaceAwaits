package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.ashley.core.Engine;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.ParallaxSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.RenderSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.TickRegionSystem;

public class WorldManager {
    
    private final Engine ecsManager;
    private final WorldRenderer worldRenderer;
    private final WorldLoader worldLoader;
    
    private World currentWorld;
    
    public WorldManager() {
        this.ecsManager = new Engine();
        this.worldLoader = new WorldLoader(this);
        this.worldRenderer = new WorldRenderer();
        addDefaultECSSystems();
        SpaceAwaits.BUS.post(new WorldEvents.InitWorldManagerEvent(this.ecsManager));
    }
    
    public void updateAndRender(float delta) {
        worldLoader.loadChunks(delta);
        ecsManager.update(delta);
        worldRenderer.render(delta);
    }
    
    private void addDefaultECSSystems() {//Create some indexed hook system thing instead?
        this.ecsManager.addSystem(new RenderSystem(worldRenderer));
        this.ecsManager.addSystem(new PlayerInputSystem());
        this.ecsManager.addSystem(new TickRegionSystem());
        this.ecsManager.addSystem(new PhysicsSystem());
        this.ecsManager.addSystem(new CameraSystem());
        this.ecsManager.addSystem(new ParallaxSystem());
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
    
    public WorldRenderer getRenderer() {
        return worldRenderer;
    }
    
    public void dispose() {
        this.worldRenderer.dispose();
    }
}
