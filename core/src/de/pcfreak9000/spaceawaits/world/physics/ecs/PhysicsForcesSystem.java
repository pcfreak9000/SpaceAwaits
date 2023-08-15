package de.pcfreak9000.spaceawaits.world.physics.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import de.pcfreak9000.spaceawaits.player.Player.GameMode;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.Components;

public class PhysicsForcesSystem extends IteratingSystem {
    
    private final World world;
    
    public PhysicsForcesSystem(World world) {
        super(Family.all(PhysicsComponent.class).get());
        this.world = world;
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent comp = Components.PHYSICS.get(entity);
        if (comp.body.getBody().getType() == BodyType.StaticBody) {
            return;
        }
        if (Components.PLAYER_INPUT.has(entity)
                && Components.PLAYER_INPUT.get(entity).player.getGameMode() == GameMode.Testing) {
            return;
        }
        comp.body.applyAccelerationPh(0, -9.81f);
        //Problem 1: welche Fixture/Shape/Rectangle für buoyancy nehmen? Bedenke mehrere Fixtures haben nebeneffekte von zu viel drag etc...
        //Problem 2: Was ist mit Kreisen?
    }
}
