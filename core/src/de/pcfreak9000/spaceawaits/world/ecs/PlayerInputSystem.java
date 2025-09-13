package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.player.Player.GameMode;
import de.pcfreak9000.spaceawaits.world.WorldEvents.PlayerJumpEvent;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsComponent;

public class PlayerInputSystem extends IteratingSystem {
    
    // How was that input multiplexing going again?! well it needs to be THIS way as
    // other important things rely on InptMgr...
    // private boolean inGui;
    
    public PlayerInputSystem() {
        super(Family.all(PlayerInputComponent.class, PhysicsComponent.class).get());
    }
    
    @Override
    protected void processEntity(Entity entity, float dt) {
        Player player = Components.PLAYER_INPUT.get(entity).player;
        // move this somewhere else...
        player.getTileWorldPlayer().dropQueue(getEngine());
        
        PlayerInputComponent play = Components.PLAYER_INPUT.get(entity);
        float vy = 0;
        float vx = 0;
        boolean onSolidGround = Components.ON_SOLID_GROUND.get(entity).isOnSolidGround();
        boolean canmovefreely = Components.ON_SOLID_GROUND.get(entity).canMoveFreely();
        boolean up = InptMgr.WORLD.isPressed(EnumInputIds.Up);
        boolean left = InptMgr.WORLD.isPressed(EnumInputIds.Left)
                && (onSolidGround || canmovefreely || player.getGameMode().isTesting);
        boolean down = InptMgr.WORLD.isPressed(EnumInputIds.Down);
        boolean right = InptMgr.WORLD.isPressed(EnumInputIds.Right)
                && (onSolidGround || canmovefreely || player.getGameMode().isTesting);
        boolean backlayer = InptMgr.WORLD.isPressed(EnumInputIds.BackLayerMod);
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
                if (onSolidGround && !Components.ON_SOLID_GROUND.get(entity).jumping) {
                    Components.ON_SOLID_GROUND.get(entity).jumping = true;
                    PlayerJumpEvent pje = new PlayerJumpEvent(player);
                    SpaceAwaits.BUS.post(pje);
                    System.out.println(pje.strength);
                    vy += play.maxYv * 10 * pje.strength;
                } else if (canmovefreely) {
                    vy += play.maxXv;
                }
            } else {
                Components.ON_SOLID_GROUND.get(entity).jumping = false;
            }
            // kinda useless, use for sneaking/ladders instead?
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
            pc.body.applyAccelerationW(vx, vy * (canmovefreely ? 6 : 3));
        }
        
        // Move the gamemode stuff?
        // ************************************************************************
        PhysicsComponent comp = Components.PHYSICS.get(entity);
        GameMode mode = Components.PLAYER_INPUT.get(entity).player.getGameMode();
        comp.affectedByForces = !mode.isTesting;
        if (mode == GameMode.TestingGhost && comp.i_nonsensorfixtures == null) {
            comp.i_nonsensorfixtures = new Array<Fixture>();
            for (Fixture f : Components.PHYSICS.get(entity).body.getBody().getFixtureList()) {
                if (!f.isSensor()) {
                    comp.i_nonsensorfixtures.add(f);
                    f.setSensor(true);
                }
            }
        } else if (mode != GameMode.TestingGhost && comp.i_nonsensorfixtures != null) {
            for (int i = 0; i < comp.i_nonsensorfixtures.size; i++) {
                comp.i_nonsensorfixtures.get(i).setSensor(false);
            }
            comp.i_nonsensorfixtures = null;
        }
        // ************************************************************************
    }
    
}
