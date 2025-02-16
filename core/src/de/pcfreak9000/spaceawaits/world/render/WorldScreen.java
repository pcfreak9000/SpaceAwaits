package de.pcfreak9000.spaceawaits.world.render;

import de.pcfreak9000.spaceawaits.command.ICommandContext;
import de.pcfreak9000.spaceawaits.core.ecs.content.GuiOverlaySystem;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.core.screen.GuiHelper;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldCombined;
import de.pcfreak9000.spaceawaits.world.command.WorldCommandContext;

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
    public void setGuiCurrent(GuiOverlay guiOverlay) {
        world.getSystem(GuiOverlaySystem.class).setGuiCurrent(guiOverlay);
    }
}
