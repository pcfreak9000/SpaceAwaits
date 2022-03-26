package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.pcfreak9000.spaceawaits.command.ICommandContext;
import de.pcfreak9000.spaceawaits.command.WorldCommandContext;
import de.pcfreak9000.spaceawaits.menu.GuiHelper;
import de.pcfreak9000.spaceawaits.menu.Hud;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;

public class WorldView implements View {
    public static final int VISIBLE_TILES_MIN = 20;
    public static final int VISIBLE_TILES_MAX = 6 * VISIBLE_TILES_MIN;
    
    private Hud hud;
    
    private World world;
    
    private OrthographicCamera camera;
    private Viewport viewport;
    
    private WorldCommandContext commands;
    
    public WorldView(GuiHelper guiHelper) {
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(VISIBLE_TILES_MIN, VISIBLE_TILES_MIN, VISIBLE_TILES_MAX, VISIBLE_TILES_MAX,
                camera);
        //this.viewport = new FitViewport(1920 / 24, 1080 / 24, camera);//Problematic because of mouse stuff?
        this.hud = new Hud(guiHelper);
        this.commands = new WorldCommandContext();
    }
    
    public void setPlayer(Player player) {
        this.hud.setPlayer(player);
    }
    
    public void setWorld(World world) {
        this.world = world;
    }
    
    @Override
    public OrthographicCamera getCamera() {
        return camera;
    }
    
    @Override
    public Viewport getViewport() {
        return viewport;
    }
    
    @Override
    public void updateAndRenderContent(float delta, boolean gui) {
        this.world.update(delta);
        if (gui) {
            this.hud.actAndDraw(delta);
        }
    }
    
    @Override
    public ICommandContext getCommandContext() {
        return commands;
    }
    
}
