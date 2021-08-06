package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.menu.GuiHelper;
import de.pcfreak9000.spaceawaits.menu.Hud;
import de.pcfreak9000.spaceawaits.world.World;

public class WorldView implements View {
    
    private Hud hud;
    
    private World world;
    
    private OrthographicCamera camera;
    private FitViewport viewport;
    
    public WorldView(GuiHelper guiHelper) {
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(1920 / 24, 1080 / 24, camera);//Problematic because of mouse stuff?
        this.hud = new Hud(guiHelper);
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
    public void updateAndRenderContent(float delta) {
        this.world.update(delta);
        this.hud.actAndDraw(delta);
    }
    
}
