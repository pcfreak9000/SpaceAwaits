package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.CoreResources.EnumDefInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.WorldManager;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.WorldRenderer;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class PlayerInputSystem extends IteratingSystem {
    
    public PlayerInputSystem() {
        super(Family.all(PlayerInputComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    private final ComponentMapper<PlayerInputComponent> mapper = ComponentMapper.getFor(PlayerInputComponent.class);
    private final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    
    private WorldManager worldManager;
    private WorldRenderer worldRend;
    
    @EventSubscription
    public void settwevent(WorldEvents.SetWorldEvent ev) {
        this.worldManager = ev.worldMgr;
        this.worldRend = SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer();//FIXME ffs
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerInputComponent play = this.mapper.get(entity);
        float vy = 0;
        float vx = 0;
        //if (physicsMapper.get(entities.get(0)).onGround) {
        boolean up = InptMgr.isPressed(EnumDefInputIds.Up);
        boolean left = InptMgr.isPressed(EnumDefInputIds.Left);
        boolean down = InptMgr.isPressed(EnumDefInputIds.Down);
        boolean right = InptMgr.isPressed(EnumDefInputIds.Right);
        boolean explode = InptMgr.isPressed(EnumDefInputIds.TestExplodeTiles);
        boolean destroy = InptMgr.isPressed(EnumDefInputIds.BreakAttack);
        int hotbarChecked = checkSelectHotbarSlot(play.player.getInventory().getSelectedSlot());
        play.player.getInventory().setSelectedSlot(hotbarChecked);
        if (up) {
            vy += play.maxYv * 5;
        }
        //kinda useless, use for sneaking/ladders instead?
        if (down) {
            vy -= play.maxYv * 5;
        }
        //}
        if (left) {
            vx -= play.maxXv * 5;
        }
        if (right) {
            vx += play.maxXv * 5;
        }
        this.physicsMapper.get(entity).body.applyAccelerationW(vx * 3, vy * 3);
        if (explode) {
            Vector2 mouse = worldRend.getMouseWorldPos();
            int txm = Tile.toGlobalTile(mouse.x);
            int tym = Tile.toGlobalTile(mouse.y);
            final int rad = 3;
            for (int i = -rad; i <= rad; i++) {
                for (int j = -rad; j <= rad; j++) {
                    if (Mathf.square(i) + Mathf.square(j) <= Mathf.square(rad)) {
                        int tx = txm + i;
                        int ty = tym + j;
                        Tile t = worldManager.getWorldAccess().getTile(tx, ty);
                        if (t != null && t.canBreak()) {
                            worldManager.getWorldAccess().setTile(Tile.EMPTY, tx, ty);
                        }
                        
                    }
                }
            }
        }
        if (destroy) {
            Vector2 mouse = worldRend.getMouseWorldPos();
            int tx = Tile.toGlobalTile(mouse.x);
            int ty = Tile.toGlobalTile(mouse.y);
            Tile t = worldManager.getWorldAccess().getTile(tx, ty);
            if (t != null && t.canBreak()) {
                worldManager.getWorldAccess().setTile(Tile.EMPTY, tx, ty);
            }
        }
        if (InptMgr.isPressed(EnumDefInputIds.Use)) {
            //Current mouse stuff
            Vector2 mouse = worldRend.getMouseWorldPos();
            int tx = Tile.toGlobalTile(mouse.x);
            int ty = Tile.toGlobalTile(mouse.y);
            //get current item
            Player player = play.player;
            ItemStack stack = player.getInventory().getSelectedStack().cpy();
            //onItemUse
            boolean used = false;
            if (stack != null && stack.getItem() != null) {
                used = stack.getItem().onItemUse(player, stack, worldManager.getWorldAccess(), tx, ty);
                player.getInventory().setSlotContent(player.getInventory().getSelectedSlot(), stack);
            }
            if (!used) {
                Tile clicked = worldManager.getWorldAccess().getTile(tx, ty);
                //onTileUse...
                used = clicked.onTileUse(player, worldManager.getWorldAccess(), stack, tx, ty);
            }
        }
        PhysicsComponent pc = physicsMapper.get(entity);
        pc.body.applyAccelerationPh(-pc.body.getLinearVelocityPh().x * 1.5f, -pc.body.getLinearVelocityPh().y * 1.5f);
    }
    
    private int checkSelectHotbarSlot(int current) {
        for (int i = Keys.NUM_1; i <= Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                return i - Keys.NUM_1;
            }
        }
        float scroll = InptMgr.getScrollY();
        int v = (int) Math.signum(scroll);
        int select = current + v;
        if (select < 0) {
            return select + 9;
        }
        return select % 9;
    }
}
