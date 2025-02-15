package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;

public class CameraSystem extends IteratingSystem {
    public static final int VISIBLE_TILES_MIN = 35;
    public static final int VISIBLE_TILES_MAX = 6 * VISIBLE_TILES_MIN;
    
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    
    private float zoom = 1;
    
    private World world;
    private GameScreen screen;
    
    public CameraSystem(World world, GameScreen screen) {
        super(Family.all(PlayerInputComponent.class, TransformComponent.class).get());
        this.world = world;
        this.screen = screen;
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(VISIBLE_TILES_MIN, VISIBLE_TILES_MIN, VISIBLE_TILES_MAX, VISIBLE_TILES_MAX,
                camera);
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    private void ev(RendererEvents.ResizeWorldRendererEvent ev) {
        this.viewport.update(ev.widthNew, ev.heightNew);
    }
    
    public OrthographicCamera getCamera() {
        return camera;
    }
    
    public Viewport getViewport() {
        return viewport;
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
    //Dedicated camera component at some time?
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = Components.TRANSFORM.get(entity);
        PlayerInputComponent pc = Components.PLAYER_INPUT.get(entity);
        float x = tc.position.x + pc.offx;
        float y = tc.position.y + pc.offy;
        if (!pc.player.getGameMode().isTesting) {
            x = Mathf.max(camera.viewportWidth / 2, x);
            y = Mathf.max(camera.viewportHeight / 2, y);
            x = Mathf.min(world.getBounds().getWidth() - camera.viewportWidth / 2, x);
            y = Mathf.min(world.getBounds().getHeight() - camera.viewportHeight / 2, y);
        }
        camera.position.set(x, y, 0);
        //TODO applying viewport at correct place...
        screen.applyViewport();
    }
}
