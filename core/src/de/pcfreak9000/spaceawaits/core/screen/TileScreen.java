package de.pcfreak9000.spaceawaits.core.screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.ecs.SystemResolver;
import de.pcfreak9000.spaceawaits.core.ecs.content.AutosaveSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.FollowMouseSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.GuiOverlaySystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.ParallaxSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.RandomSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.RandomTickSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.SelectorSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.TickCounterSystem;
import de.pcfreak9000.spaceawaits.generation.IGeneratingLayer;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.save.IWorldSave;
import de.pcfreak9000.spaceawaits.save.WorldMeta;
import de.pcfreak9000.spaceawaits.world.IChunkLoader;
import de.pcfreak9000.spaceawaits.world.IGlobalLoader;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkSystem;
import de.pcfreak9000.spaceawaits.world.chunk.mgmt.FollowingTicket;
import de.pcfreak9000.spaceawaits.world.chunk.mgmt.ITicket;
import de.pcfreak9000.spaceawaits.world.command.WorldCommandContext;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.EntityInteractSystem;
import de.pcfreak9000.spaceawaits.world.ecs.InventoryHandlerSystem;
import de.pcfreak9000.spaceawaits.world.ecs.OnSolidGroundComponent;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.world.ecs.TilesActivatorSystem;
import de.pcfreak9000.spaceawaits.world.ecs.WorldSystem;
import de.pcfreak9000.spaceawaits.world.gen.GeneratorSettings;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsDebugRendererSystem;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsForcesSystem;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderSystem;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TileScreen extends GameScreen {

    private IGlobalLoader globalLoader;
    private IChunkLoader chunkLoader;

    private final IWorldSave save;

    public TileScreen(GuiHelper guihelper, IWorldSave save) {
        super(guihelper, new WorldCommandContext());
        this.save = save;
    }

    public String getUUID() {
        return save.getUUID();
    }

    @Override
    public void load() {
        WorldMeta meta = save.getWorldMeta();
        String genId = meta.getWorldGeneratorUsed();
        long worldSeed = meta.getWorldSeed();
        // No default because this is crucial information and can't really be defaulted
        IGeneratingLayer<WorldPrimer, GeneratorSettings> gen = (IGeneratingLayer<WorldPrimer, GeneratorSettings>) Registry.GENERATOR_REGISTRY
                .get(genId);
        WorldPrimer worldPrimer = gen.generate(new GeneratorSettings(worldSeed));
        worldPrimer.setWorldBounds(meta.getBounds());
        this.globalLoader = save.createGlobalLoader();
        this.chunkLoader = save.createChunkLoader();
        setupECS(worldPrimer);
        super.load();
    }

    private void setupECS(WorldPrimer primer) {
        SystemResolver ecs = new SystemResolver();
        ecs.addSystem(new WorldSystem(globalLoader, primer.getWorldGenerator(), primer.getWorldBounds(),
                primer.getWorldProperties(), primer.getLightProvider(), primer.getPlayerSpawn()));
        ecs.addSystem(new InventoryHandlerSystem());
        ecs.addSystem(new TileSystem());
        ecs.addSystem(new PlayerInputSystem());
        ecs.addSystem(new SelectorSystem());
        ecs.addSystem(new TilesActivatorSystem());
        ecs.addSystem(new FollowMouseSystem());
        ecs.addSystem(new EntityInteractSystem());
        ecs.addSystem(new PhysicsForcesSystem());
        ecs.addSystem(new PhysicsSystem());
        ecs.addSystem(new ChunkSystem(chunkLoader, primer.getChunkGenerator(), primer.getWorldBounds(),
                primer.getWorldProperties()));
        ecs.addSystem(new CameraSystem(primer.getWorldBounds(), getRenderHelper()));
        ecs.addSystem(new ParallaxSystem(CameraSystem.VISIBLE_TILES_MIN));
        ecs.addSystem(new RenderSystem(ecsEngine, this));
        ecs.addSystem(new PhysicsDebugRendererSystem());
        ecs.addSystem(new TickCounterSystem());
        ecs.addSystem(new RandomTickSystem());
        ecs.addSystem(new GuiOverlaySystem(this));
        ecs.addSystem(new RandomSystem(new RandomXS128()));
        ecs.addSystem(new AutosaveSystem(10f));
        SpaceAwaits.BUS.post(new WorldEvents.SetupEvent(ecs, getWorldBus()));

        // this one needs some stuff with topological sort anyways to resolve
        // dependencies etc
        ecs.setupSystems(ecsEngine);
    }

    @Override
    public void unload() {
        super.unload();

        this.chunkLoader.finish();
        this.globalLoader.finish();
    }

    public void addTicket(ITicket ticket) {
        ecsEngine.getSystem(ChunkSystem.class).addTicket(ticket);
    }

    public void removeTicket(ITicket ticket) {
        ecsEngine.getSystem(ChunkSystem.class).removeTicket(ticket);
    }

    // TMP??
    @Deprecated
    public Engine getECS() {
        return ecsEngine;
    }
}
