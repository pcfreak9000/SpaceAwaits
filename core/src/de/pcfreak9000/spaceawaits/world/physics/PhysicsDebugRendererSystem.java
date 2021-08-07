package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Disposable;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

public class PhysicsDebugRendererSystem extends EntitySystem implements Disposable {
    
    //maybe there is a better way of doing this, seems kinda crude to injekt the PhysicsSystem...
    
    private Box2DDebugRenderer debugRend;
    private Camera cam;
    private PhysicsSystemBox2D phsystem;
    
    private boolean enabled;
    
    public PhysicsDebugRendererSystem(PhysicsSystemBox2D sys, GameRenderer renderer) {
        this.debugRend = new Box2DDebugRenderer(true, true, true, true, true, true);
        this.phsystem = sys;
        this.cam = renderer.getView().getCamera();
    }
    
    @Override
    public void update(float deltaTime) {
        if (InptMgr.isJustPressed(EnumInputIds.DebugDrawPhysics)) {
            enabled = !enabled;
        }
        if (enabled) {
            OrthographicCamera cam = new OrthographicCamera();
            cam.setToOrtho(false, PhysicsSystemBox2D.METER_CONV.in(this.cam.viewportWidth),
                    PhysicsSystemBox2D.METER_CONV.in(this.cam.viewportHeight));
            cam.position.set(PhysicsSystemBox2D.METER_CONV.in(this.cam.position.x),
                    PhysicsSystemBox2D.METER_CONV.in(this.cam.position.y), 0);
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
