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
import de.pcfreak9000.spaceawaits.world.ITileBreaker;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.WorldRenderer;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class PlayerInputSystem extends IteratingSystem {
    
    private final World world;
    
    public PlayerInputSystem(World world) {
        super(Family.all(PlayerInputComponent.class, TransformComponent.class, PhysicsComponent.class).get());
        this.world = world;
        SpaceAwaits.BUS.register(this);
    }
    
    private static final ComponentMapper<PlayerInputComponent> mapper = ComponentMapper
            .getFor(PlayerInputComponent.class);
    private static final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper
            .getFor(PhysicsComponent.class);
    private static final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    
    private WorldRenderer worldRend;
    
    @EventSubscription
    public void settwevent(WorldEvents.SetWorldEvent ev) {
        this.worldRend = SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer();//FIXME ffs
    }
    
    private final ITileBreaker br = new ITileBreaker() {
        
        @Override
        public void onBreak(int tx, int ty, TileLayer layer, Tile tile, World world) {
        }
        
        @Override
        public float getSpeed() {
            return 1;
        }
        
        @Override
        public float getMaterialLevel() {
            return 0;
        }
    };
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerInputComponent play = mapper.get(entity);
        float vy = 0;
        float vx = 0;
        //        Vector2 transform = transformMapper.get(entity).position;
        Vector2 mouse = worldRend.getMouseWorldPos();
        //if (physicsMapper.get(entities.get(0)).onGround) {
        boolean up = InptMgr.isPressed(EnumDefInputIds.Up);
        boolean left = InptMgr.isPressed(EnumDefInputIds.Left);
        boolean down = InptMgr.isPressed(EnumDefInputIds.Down);
        boolean right = InptMgr.isPressed(EnumDefInputIds.Right);
        boolean explode = InptMgr.isPressed(EnumDefInputIds.TestExplodeTiles);
        //        world.raycastTiles(new IRaycastTileCallback() {
        //            
        //            @Override
        //            public boolean reportRayTile(Tile tile, int tx, int ty) {
        //                if (tile != Tile.EMPTY) {
        //                    System.out.println(tile);
        //                }
        //                return tile == Tile.EMPTY;
        //            }
        //        }, transform.x + 1, transform.y + 2, mouse.x, mouse.y, TileLayer.Front);
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
        physicsMapper.get(entity).body.applyAccelerationW(vx * 3, vy * 3);
        if (explode) {
            int txm = Tile.toGlobalTile(mouse.x);
            int tym = Tile.toGlobalTile(mouse.y);
            final int rad = 3;
            for (int i = -rad; i <= rad; i++) {
                for (int j = -rad; j <= rad; j++) {
                    if (Mathf.square(i) + Mathf.square(j) <= Mathf.square(rad)) {
                        int tx = txm + i;
                        int ty = tym + j;
                        Tile t = world.getTile(tx, ty, TileLayer.Front);
                        if (t != null && t.canBreak()) {
                            world.setTile(tx, ty, TileLayer.Front, Tile.EMPTY);
                        }
                        
                    }
                }
            }
        }
        if (InptMgr.isPressed(EnumDefInputIds.BreakAttack)) {
            int tx = Tile.toGlobalTile(mouse.x);
            int ty = Tile.toGlobalTile(mouse.y);
            Player player = play.player;
            ItemStack stack = player.getInventory().getSelectedStack().cpy();
            boolean used = false;
            if (stack != null && stack.getItem() != null) {
                used = stack.getItem().onItemAttack(player, stack, world, tx, ty, mouse.x, mouse.y);
                player.getInventory().setSlotContent(player.getInventory().getSelectedSlot(), stack);
            }
            if (!used) {
                world.breakTile(tx, ty, TileLayer.Front, br);
            }
        }
        if (InptMgr.isPressed(EnumDefInputIds.Use)) {
            //Current mouse stuff
            int tx = Tile.toGlobalTile(mouse.x);
            int ty = Tile.toGlobalTile(mouse.y);
            //get current item
            Player player = play.player;
            boolean used = false;
            ItemStack stack = null;
            if (player.getInventory().getSelectedStack() != null
                    && !player.getInventory().getSelectedStack().isEmpty()) {
                stack = player.getInventory().getSelectedStack().cpy();
                //onItemUse
                if (stack != null && stack.getItem() != null) {
                    used = stack.getItem().onItemUse(player, stack, world, tx, ty, mouse.x, mouse.y);
                    player.getInventory().setSlotContent(player.getInventory().getSelectedSlot(), stack);
                }
            }
            if (!used) {
                Tile clicked = world.getTile(tx, ty, TileLayer.Front);
                //onTileUse
                used = clicked.onTileUse(player, world, stack, tx, ty);
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
