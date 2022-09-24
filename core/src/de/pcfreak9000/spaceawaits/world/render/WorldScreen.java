package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.pcfreak9000.spaceawaits.command.ICommandContext;
import de.pcfreak9000.spaceawaits.command.WorldCommandContext;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.gui.Hud;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.screen.GuiHelper;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldCombined;

public class WorldScreen extends GameScreen {
    public static final int VISIBLE_TILES_MIN = 35;
    public static final int VISIBLE_TILES_MAX = 6 * VISIBLE_TILES_MIN;
    
    private Hud hud;
    
    private World world;
    
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    
    private WorldCommandContext commands;
    
    private float zoom = 1;
    
    public WorldScreen(GuiHelper guiHelper, WorldCombined world, Player player) {
        super(guiHelper);
        this.world = world;
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(VISIBLE_TILES_MIN, VISIBLE_TILES_MIN, VISIBLE_TILES_MAX, VISIBLE_TILES_MAX,
                camera);
        this.hud = new Hud(guiHelper);
        this.commands = new WorldCommandContext();
        //Hmmmm
        world.initRenderableWorld(this);
        hud.setPlayer(player);
    }
    
    public void changeZoom(float f) {
        setZoom(zoom + f);
    }
    
    public void resetZoom() {
        setZoom(1);
    }
    
    private void setZoom(float f) {
        zoom = MathUtils.clamp(f, 0.075f, SpaceAwaits.DEBUG ? 4f : 2.5f);
        this.viewport.setMaxWorldHeight(VISIBLE_TILES_MAX * zoom);
        this.viewport.setMaxWorldWidth(VISIBLE_TILES_MAX * zoom);
        this.viewport.setMinWorldHeight(VISIBLE_TILES_MIN * zoom);
        this.viewport.setMinWorldWidth(VISIBLE_TILES_MIN * zoom);
        this.viewport.update(this.viewport.getScreenWidth(), this.viewport.getScreenHeight());
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
