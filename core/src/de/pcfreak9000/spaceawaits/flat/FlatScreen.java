package de.pcfreak9000.spaceawaits.flat;

import de.pcfreak9000.spaceawaits.command.ICommandContext;
import de.pcfreak9000.spaceawaits.core.ecs.SystemResolver;
import de.pcfreak9000.spaceawaits.core.ecs.content.GuiOverlaySystem;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.core.screen.GuiHelper;
import de.pcfreak9000.spaceawaits.world.ecs.InventoryHandlerSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderSystem;

public class FlatScreen extends GameScreen {

    public FlatScreen(GuiHelper guiHelper) {
        super(guiHelper, null);
    }

    @Override
    public void load() {
        setupECS();
        super.load();
    }

    private void setupECS() {
        SystemResolver ecs = new SystemResolver();
        ecs.addSystem(new InventoryHandlerSystem());
        ecs.addSystem(new CameraSystem(null, getRenderHelper()));
        ecs.addSystem(new RenderSystem(ecsEngine, this));
        ecs.addSystem(new GuiOverlaySystem(this));
        ecs.setupSystems(ecsEngine);
    }
}
