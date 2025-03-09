package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sudoplay.joise.module.ModuleBasisFunction;
import com.sudoplay.joise.module.ModuleBasisFunction.BasisType;
import com.sudoplay.joise.module.ModuleBasisFunction.InterpolationType;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.core.screen.RenderHelper2D;
import de.pcfreak9000.spaceawaits.util.Bounds;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;

public class CameraSystem extends IteratingSystem {
    public static final int VISIBLE_TILES_MIN = 35;
    public static final int VISIBLE_TILES_MAX = 6 * VISIBLE_TILES_MIN;

    private OrthographicCamera camera;
    private ExtendViewport viewport;

    private float zoom = 1;
//    private float shake = 0;
//    private float xsh, ysh, rotsh;

    private Bounds bounds;
    private Vector2 mousePosVec = new Vector2();

    public CameraSystem(Bounds bounds, RenderHelper2D renderHelper2D) {
        super(Family.all(PlayerInputComponent.class, TransformComponent.class).get());
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(VISIBLE_TILES_MIN, VISIBLE_TILES_MIN, VISIBLE_TILES_MAX, VISIBLE_TILES_MAX,
                camera);
        renderHelper2D.setViewport(viewport);
        //ModuleBasisFunction m = new ModuleBasisFunction(BasisType.SIMPLEX, InterpolationType.LINEAR);
    }

    @EventSubscription
    private void ev2(RendererEvents.PreFrameEvent ev) {
        updateMouseWorldPosCache();
    }

    @EventSubscription
    private void ev(RendererEvents.ResizeWorldRendererEvent ev) {
        this.viewport.update(ev.widthNew, ev.heightNew);
    }

    // Move to some InputSystem?
    private void updateMouseWorldPosCache() {
        mousePosVec.set(Gdx.input.getX(), Gdx.input.getY());
        mousePosVec = this.getViewport().unproject(mousePosVec);
    }

    public Vector2 getMouseWorldPos() {
        return mousePosVec;
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
        zoom = MathUtils.clamp(f, 0.075f, SpaceAwaits.DEBUG ? 3.75f : 2.5f);
        this.viewport.setMaxWorldHeight(VISIBLE_TILES_MAX * zoom);
        this.viewport.setMaxWorldWidth(VISIBLE_TILES_MAX * zoom);
        this.viewport.setMinWorldHeight(VISIBLE_TILES_MIN * zoom);
        this.viewport.setMinWorldWidth(VISIBLE_TILES_MIN * zoom);
        this.viewport.update(this.viewport.getScreenWidth(), this.viewport.getScreenHeight());
    }
    // Dedicated camera component at some time?

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent tc = Components.TRANSFORM.get(entity);
        PlayerInputComponent pc = Components.PLAYER_INPUT.get(entity);
        float x = tc.position.x + pc.offx;
        float y = tc.position.y + pc.offy;
        if (this.bounds != null && !pc.player.getGameMode().isTesting) {
            x = Mathf.max(bounds.getTileX() + camera.viewportWidth / 2, x);
            y = Mathf.max(bounds.getTileY() + camera.viewportHeight / 2, y);
            x = Mathf.min(bounds.getWidth() - camera.viewportWidth / 2, x);
            y = Mathf.min(bounds.getHeight() - camera.viewportHeight / 2, y);
        }
        camera.position.set(x, y, 0);
        if (InptMgr.WORLD.isPressed(EnumInputIds.CamZoom)) {
            float scroll = InptMgr.getScrollY() * 0.1f;
            changeZoom(scroll);
        }
    }
}
