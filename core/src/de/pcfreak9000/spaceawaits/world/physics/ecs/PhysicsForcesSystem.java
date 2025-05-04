package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

import de.pcfreak9000.spaceawaits.player.Player.GameMode;
import de.pcfreak9000.spaceawaits.world.ecs.Components;

public class PhysicsForcesSystem extends IteratingSystem {

    public PhysicsForcesSystem() {
        super(Family.all(PhysicsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent comp = Components.PHYSICS.get(entity);
        if (comp.body.getBody().getType() == BodyType.StaticBody) {
            return;
        }
        if (!comp.affectedByForces) {
            return;
        }
        boolean canmovefreely = Components.ON_SOLID_GROUND.has(entity) ? Components.ON_SOLID_GROUND.get(entity).canMoveFreely() : false;
        comp.body.applyAccelerationPh(0, -9.81f);
        //eeeehhh.......
        //comp.body.applyAccelerationPh(-comp.body.getLinearVelocityPh().x * 4f,
          //      -comp.body.getLinearVelocityPh().y * (canmovefreely ? 40f : 0.1f));
        // Problem 1: welche Fixture/Shape/Rectangle für buoyancy nehmen? Bedenke
        // mehrere Fixtures haben nebeneffekte von zu viel drag etc...
        // Problem 2: Was ist mit Kreisen?
    }
}
