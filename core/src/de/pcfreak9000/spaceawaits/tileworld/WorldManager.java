package de.pcfreak9000.spaceawaits.tileworld;

import java.util.Objects;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ScreenAdapter;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.ParallaxSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.RenderSystem;
import de.pcfreak9000.spaceawaits.tileworld.ecs.TickRegionSystem;

public class WorldManager extends ScreenAdapter {
    
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
    
    @Override
    public void render(float delta) {
        worldLoader.loadChunks(delta);
        ecsManager.update(delta);
        //worldRenderer.render(delta);
    }
    
    private void addDefaultECSSystems() {
        //        PostprocessingBundle bund = new PostprocessingBundle();
        //        bund.add(new BrightnessAccent());
        //        bund.add(GaussianBlur.createGaussianBlurBundle(1));
        //        bund.add(GaussianBlur.createGaussianBlurBundle(0.85f));
        //        bund.add(GaussianBlur.createGaussianBlurBundle(0.4f));
        //        EffectMixer eff = new EffectMixer(bund);
        //        eff.setWeightSource(0.6f);
        //        eff.setWeightEffect(0.5f);
        // this.viewManager.getMainView().setPostprocessor(eff);
        this.ecsManager.addSystem(new RenderSystem(worldRenderer));
        this.ecsManager.addSystem(new PlayerInputSystem());
        this.ecsManager.addSystem(new TickRegionSystem());
        this.ecsManager.addSystem(new PhysicsSystem());
        this.ecsManager.addSystem(new CameraSystem());
        this.ecsManager.addSystem(new ParallaxSystem());
        //this.ecsManager.addSystem(new FogSystem());
    }
    
    public void setWorld(World world) {
        Objects.requireNonNull(world);
        SpaceAwaits.BUS.post(new WorldEvents.SetWorldEvent(this, currentWorld, world));
        this.currentWorld = world;
        this.worldLoader.setWorld(this.currentWorld);
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
}
