package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Disposable;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.RenderSystemMarker;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;

public class PhysicsDebugRendererSystem extends EntitySystem implements Disposable, RenderSystemMarker {
    
    //maybe there is a better way of doing this, seems kinda crude to injekt the PhysicsSystem...
    
    private Box2DDebugRenderer debugRend;
    private Camera cam;
    private PhysicsSystem phsystem;
    
    private boolean enabled;
    
    public PhysicsDebugRendererSystem(PhysicsSystem sys, GameScreen renderer) {
        this.debugRend = new Box2DDebugRenderer(true, true, true, true, true, true);
        this.phsystem = sys;
    }
    
    @Override
    public void update(float deltaTime) {
        if (InptMgr.isJustPressed(EnumInputIds.DebugDrawPhysics)) {
            enabled = !enabled;
        }
        if (enabled) {
            this.cam = getEngine().getSystem(CameraSystem.class).getCamera();
            OrthographicCamera cam = new OrthographicCamera();
            cam.setToOrtho(false, PhysicsSystem.METER_CONV.in(this.cam.viewportWidth),
                    PhysicsSystem.METER_CONV.in(this.cam.viewportHeight));
            cam.position.set(PhysicsSystem.METER_CONV.in(this.cam.position.x),
                    PhysicsSystem.METER_CONV.in(this.cam.position.y), 0);
            cam.update();
            debugRend.render(phsystem.getB2DWorld(), cam.combined);
        }
    }
    
    @Override
    public void dispose() {
        Logger.getLogger(getClass()).debug("Disposing...");
        debugRend.dispose();
    }
}
