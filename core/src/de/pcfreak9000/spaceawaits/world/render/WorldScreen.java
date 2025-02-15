package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.pcfreak9000.spaceawaits.command.ICommandContext;
import de.pcfreak9000.spaceawaits.core.ecs.content.GuiOverlaySystem;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.core.screen.GuiHelper;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldCombined;
import de.pcfreak9000.spaceawaits.world.command.WorldCommandContext;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;

public class WorldScreen extends GameScreen {
    
    private WorldCommandContext commands;
    
    private World world;
    
    public WorldScreen(GuiHelper guiHelper, WorldCombined world, Player player) {
        super(guiHelper);
        this.world = world;
        
        this.commands = new WorldCommandContext();
        //Hmmmm
        world.initRenderableWorld(this);
    }
    
    @Deprecated
    @Override
    public OrthographicCamera getCamera() {
        return world.getSystem(CameraSystem.class).getCamera();
    }
    
    @Deprecated
    @Override
    public Viewport getViewport() {
        return world.getSystem(CameraSystem.class).getViewport();
    }
    
    @Override
    public void updateAndRenderContent(float delta, boolean gui) {
        this.world.update(delta);
    }
    
    @Override
    public ICommandContext getCommandContext() {
        return commands;
    }
    
    @Deprecated
    @Override
    public boolean isGuiContainerOpen() {
        return world.getSystem(GuiOverlaySystem.class).isGuiContainerOpen();
    }
    
    @Deprecated
    @Override
    public void setGuiCurrent(GuiOverlay guiOverlay) {
        world.getSystem(GuiOverlaySystem.class).setGuiCurrent(guiOverlay);
    }
}
