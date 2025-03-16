package de.pcfreak9000.spaceawaits.flat;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.command.ICommandContext;
import de.pcfreak9000.spaceawaits.core.ecs.SystemResolver;
import de.pcfreak9000.spaceawaits.core.ecs.content.ActivatorSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.AutosaveSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.GuiChatSystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.GuiOverlaySystem;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.core.screen.GuiHelper;
import de.pcfreak9000.spaceawaits.world.command.WorldCommandContext;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.InventoryHandlerSystem;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputSystem;
import de.pcfreak9000.spaceawaits.world.ecs.TilesActivatorSystem;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsDebugRendererSystem;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderSystem;

public class FlatScreen extends GameScreen {

    public FlatScreen(GuiHelper guiHelper) {
        super(guiHelper, new WorldCommandContext());
    }

    @Override
    public void load() {
        setupECS();
        super.load();
    }

    private void setupECS() {
        SystemResolver ecs = new SystemResolver();
        ecs.addSystem(new FlatTestWorldSystem());
        ecs.addSystem(new InventoryHandlerSystem());
        ecs.addSystem(new GuiChatSystem());
        ecs.addSystem(new PlayerInputSystem());
        ecs.addSystem(new TilesActivatorSystem());
        ecs.addSystem(new PhysicsSystem());
        ecs.addSystem(new CameraSystem(null, getRenderHelper()));
        RenderSystem rsys = new RenderSystem(ecsEngine, this, null);
        rsys.setAlwaysForceSort(true);
        rsys.setComparator(RenderSystem.DEFAULT_COMPARATOR.thenComparing((e1, e2) -> {
            TransformComponent t1 = Components.TRANSFORM.get(e1);
            TransformComponent t2 = Components.TRANSFORM.get(e2);
            float f = t2.position.y - t1.position.y;
            return (int) Math.signum(f);
        }));
        ecs.addSystem(rsys);
        ecs.addSystem(new PhysicsDebugRendererSystem());
        ecs.addSystem(new GuiOverlaySystem(this));
        ecs.addSystem(new AutosaveSystem(10f));
        ecs.setupSystems(ecsEngine);
    }
}
