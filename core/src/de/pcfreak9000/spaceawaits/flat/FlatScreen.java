package de.pcfreak9000.spaceawaits.flat;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import de.pcfreak9000.spaceawaits.command.ICommandContext;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.core.screen.GuiHelper;
import de.pcfreak9000.spaceawaits.gui.GuiOverlay;
import de.pcfreak9000.spaceawaits.gui.Hud;
import de.pcfreak9000.spaceawaits.world.command.WorldCommandContext;

public class FlatScreen extends GameScreen {
    public static final int VISIBLE_TILES_MIN = 35;
    public static final int VISIBLE_TILES_MAX = 6 * VISIBLE_TILES_MIN;
    
    private Hud hud;
    
    private FlatWorld world;
    
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    
    private WorldCommandContext commands;
    
    private float zoom = 1;
    
    public FlatScreen(GuiHelper guiHelper, FlatWorld world, FlatPlayer player) {
        super(guiHelper);
        this.world = world;
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(VISIBLE_TILES_MIN, VISIBLE_TILES_MIN, VISIBLE_TILES_MAX, VISIBLE_TILES_MAX,
                camera);
        this.hud = new Hud(guiHelper);
        this.commands = new WorldCommandContext();
        // Hmmmm
        world.initRenderableWorld(this);
        hud.setPlayer(player);
        world.ecsEngine.addEntity(player.getEntity());
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
    
    @Deprecated
    @Override
    public void setGuiCurrent(GuiOverlay guiOverlay) {
    }
}
