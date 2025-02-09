package de.pcfreak9000.spaceawaits.flat;

import java.util.Random;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.RandomXS128;

import de.omnikryptec.event.EventBus;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.assets.DynamicAssetListener;
import de.pcfreak9000.spaceawaits.core.assets.WatchDynamicAssetAnnotationProcessor;
import de.pcfreak9000.spaceawaits.core.ecs.ModifiedEngine;
import de.pcfreak9000.spaceawaits.core.ecs.SystemResolver;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderSystem;

public class FlatWorld {
    public static final float STEPLENGTH_SECONDS = 1 / 60f;

    protected final Engine ecsEngine;
    protected final EventBus eventBus;

    // Used for random item drops etc, not terrain gen etc
    private final RandomXS128 worldRandom;

    private int countChunkActive = 0;

    public FlatWorld() {
        // initialize fields
        this.ecsEngine = new ModifiedEngine(STEPLENGTH_SECONDS);
        this.eventBus = new EventBus();
        SpaceAwaits.BUS.register(eventBus);// Not too sure about this
        this.worldRandom = new RandomXS128();

    }

    public void initRenderableWorld(FlatScreen screen) {
        setupECS(ecsEngine, screen);

    }

    private void setupECS(Engine engine, FlatScreen gameScreen) {
        for (DynamicAssetListener<Component> dal : WatchDynamicAssetAnnotationProcessor.get()) {
            engine.addEntityListener(dal.getFamily(), dal);
        }
        SystemResolver ecs = new SystemResolver();
        RenderSystem rendersystem = new RenderSystem(null, gameScreen, eventBus);
        rendersystem.setDoLight(false);
        ecs.addSystem(rendersystem);
        // this one needs some stuff with topological sort anyways to resolve
        // dependencies etc
        // SpaceAwaits.BUS.post(new WorldEvents.SetupEntitySystemsEvent(this, ecs,
        // primer));
        ecs.setupSystems(engine);
    }

    // public abstract void unloadWorld();

    public void update(float dt) {
        this.ecsEngine.update(dt);
    }

    public Random getWorldRandom() {
        return worldRandom;
    }

    public void setPlayer(Player player) {
        ecsEngine.addEntity(player.getPlayerEntity());
    }

    // Could keep the Player instance here and just removePlayer()...
    public void removePlayer(Player player) {
        ecsEngine.removeEntity(player.getPlayerEntity());
    }

    public <T extends EntitySystem> T getSystem(Class<T> clazz) {
        return ecsEngine.getSystem(clazz);
    }

    public int getUpdatingChunksCount() {
        return countChunkActive;
    }

    public EventBus getWorldBus() {
        return this.eventBus;
    }
}
