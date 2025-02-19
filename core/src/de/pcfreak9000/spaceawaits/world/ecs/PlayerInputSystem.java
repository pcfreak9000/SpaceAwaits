package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsComponent;

public class PlayerInputSystem extends IteratingSystem {
    
    //How was that input multiplexing going again?! well it needs to be THIS way as other important things rely on InptMgr...
    // private boolean inGui;
    
    public PlayerInputSystem() {
        super(Family.all(PlayerInputComponent.class, PhysicsComponent.class).get());
    }
    
    @Override
    protected void processEntity(Entity entity, float dt) {
        Player player = Components.PLAYER_INPUT.get(entity).player;
        //move this somewhere else...
        player.dropQueue(getEngine());
        
        PlayerInputComponent play = Components.PLAYER_INPUT.get(entity);
        float vy = 0;
        float vx = 0;
        boolean up = InptMgr.WORLD.isPressed(EnumInputIds.Up);
        boolean left = InptMgr.WORLD.isPressed(EnumInputIds.Left);
        boolean down = InptMgr.WORLD.isPressed(EnumInputIds.Down);
        boolean right = InptMgr.WORLD.isPressed(EnumInputIds.Right);
        boolean backlayer = InptMgr.WORLD.isPressed(EnumInputIds.BackLayerMod);
        boolean onSolidGround = Components.ON_SOLID_GROUND.get(entity).isOnSolidGround();
        boolean canmovefreely = Components.ON_SOLID_GROUND.get(entity).canMoveFreely();
        if (InptMgr.WORLD.isJustPressed(EnumInputIds.TestButton)) {
            Components.STATS.get(entity).statDatas.get("health").current -= backlayer ? -10 : 10;
        }
        if (player.getGameMode().isTesting) {
            if (up) {
                vy += play.maxXv;
            }
            if (down) {
                vy -= play.maxXv;
            }
            if (left) {
                vx -= play.maxXv;
            }
            if (right) {
                vx += play.maxXv;
            }
            
            PhysicsComponent pc = Components.PHYSICS.get(entity);
            if (!InptMgr.WORLD.isPressed(EnumInputIds.MovMod)) {
                vx *= 0.5f;
                vy *= 0.5f;
            }
            pc.body.setVelocityW(vx, vy);
        } else {
            if (up) {
                if (onSolidGround) {
                    vy += play.maxYv * 5;
                } else if (canmovefreely) {
                    vy += play.maxXv;
                }
            }
            //kinda useless, use for sneaking/ladders instead?
            if (down) {
                if (canmovefreely) {
                    vy -= play.maxXv;
                } else {
                    vy -= play.maxYv * 2;
                }
            }
            if (left) {
                vx -= play.maxXv;
            }
            if (right) {
                vx += play.maxXv;
            }
            PhysicsComponent pc = Components.PHYSICS.get(entity);
            pc.body.applyAccelerationW(vx * 6, vy * (canmovefreely ? 6 : 3));
            pc.body.applyAccelerationPh(-pc.body.getLinearVelocityPh().x * 40,
                    -pc.body.getLinearVelocityPh().y * (canmovefreely ? 40f : 0.1f));
        }
    }
    
}
