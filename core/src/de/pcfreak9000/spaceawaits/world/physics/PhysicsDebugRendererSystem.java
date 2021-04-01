package de.pcfreak9000.spaceawaits.world.physics;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.WorldEvents;

public class PhysicsDebugRendererSystem extends EntitySystem {
    
    //maybe there is a better way of doing this, seems kinda crude to injekt the PhysicsSystem...
    
    private Box2DDebugRenderer debugRend;
    private Camera cam;
    private PhysicsSystemBox2D phsystem;
    
    public PhysicsDebugRendererSystem(PhysicsSystemBox2D sys) {
        SpaceAwaits.BUS.register(this);
        this.debugRend = new Box2DDebugRenderer(true, true, true, true, true, true);
        this.phsystem = sys;
    }
    
    @EventSubscription
    private void ev(WorldEvents.SetWorldEvent ev) {
        this.cam = SpaceAwaits.getSpaceAwaits().getScreenStateManager().getWorldRenderer().getCamera();
    }
    
    @EventSubscription
    private void ev2(CoreEvents.ExitEvent ex) {
        Logger.getLogger(getClass()).debug("Disposing...");
        debugRend.dispose();//Only feasible as long as there is only one world or something
    }
    
    @Override
    public void update(float deltaTime) {
        OrthographicCamera cam = new OrthographicCamera();
        cam.setToOrtho(false, PhysicsSystemBox2D.METER_CONV.in(this.cam.viewportWidth),
                PhysicsSystemBox2D.METER_CONV.in(this.cam.viewportHeight));
        cam.position.set(PhysicsSystemBox2D.METER_CONV.in(this.cam.position.x),
                PhysicsSystemBox2D.METER_CONV.in(this.cam.position.y), 0);
        cam.update();
        debugRend.render(phsystem.getWorld(), cam.combined);
    }
}
