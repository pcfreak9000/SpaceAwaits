package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Disposable;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.RenderSystemMarker;
import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;
@RenderSystemMarker
public class PhysicsDebugRendererSystem extends EntitySystem implements Disposable {
    
    //maybe there is a better way of doing this, seems kinda crude to inject the PhysicsSystem...
    
    //private Box2DDebugRenderer debugRend;
    private boolean enabled;
    
    private OrthographicCamera cam = new OrthographicCamera();
    
    private SystemCache<PhysicsSystem> phys = new SystemCache<>(PhysicsSystem.class);
    private SystemCache<CameraSystem> camsys = new SystemCache<>(CameraSystem.class);
    
    public PhysicsDebugRendererSystem() {
        //this.debugRend = new Box2DDebugRenderer(true, true, true, true, true, true);
    }
    
    @Override
    public void update(float deltaTime) {
        if (InptMgr.WORLD.isJustPressed(EnumInputIds.DebugDrawPhysics)) {
            enabled = !enabled;
        }
        enabled = false;
        if (enabled) {
            Camera gameecam = camsys.get(getEngine()).getCamera();
            cam.setToOrtho(false, PhysicsSystem.METER_CONV.in(gameecam.viewportWidth),
                    PhysicsSystem.METER_CONV.in(gameecam.viewportHeight));
            cam.position.set(PhysicsSystem.METER_CONV.in(gameecam.position.x),
                    PhysicsSystem.METER_CONV.in(gameecam.position.y), 0);
            cam.update();
            //debugRend.render(phys.get(getEngine()).getB2DWorld(), cam.combined);
        }
    }
    
    @Override
    public void dispose() {
        Logger.getLogger(getClass()).debug("Disposing...");
        //debugRend.dispose();
    }
}
