package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.tile.ITileBreaker;
import de.pcfreak9000.spaceawaits.world.tile.InstantBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class PlayerInputSystem extends EntitySystem {
    
    private static final ComponentMapper<PlayerInputComponent> mapper = ComponentMapper
            .getFor(PlayerInputComponent.class);
    private static final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper
            .getFor(PhysicsComponent.class);
    private static final ComponentMapper<OnSolidGroundComponent> solidGroundMapper = ComponentMapper
            .getFor(OnSolidGroundComponent.class);
    private static final ComponentMapper<HealthComponent> healthMapper = ComponentMapper.getFor(HealthComponent.class);
    
    private final World world;
    private final GameRenderer worldRend;
    
    private Player player;
    
    public PlayerInputSystem(World world, GameRenderer renderer) {
        this.world = world;
        this.worldRend = renderer;
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void joined(WorldEvents.PlayerJoinedEvent ev) {
        if (ev.world == this.world) {//TODO world specific eventbus?
            this.player = ev.player;
        }
    }
    
    private final ITileBreaker br = new ITileBreaker() {
        
        @Override
        public float getSpeed() {
            return 1;
        }
        
        @Override
        public float getMaterialLevel() {
            return 0;
        }
        
        @Override
        public void onTileBreak(int tx, int ty, TileLayer layer, Tile tile, World world, Array<ItemStack> drops,
                RandomXS128 random) {
        }
        
        @Override
        public boolean canBreak(int tx, int ty, TileLayer layer, Tile tile, World world) {
            return true;
        }
    };
    
    @Override
    public void update(float deltaTime) {
        if (this.player == null) {
            return;
        }
        if (InptMgr.isJustPressed(EnumInputIds.ToggleInventory)) {
            if (!worldRend.isGuiContainerOpen()) {
                player.openInventory();
            }
        }
        if (worldRend.isGuiContainerOpen()) {
            return;
        }
        Entity entity = this.player.getPlayerEntity();
        PlayerInputComponent play = mapper.get(entity);
        float vy = 0;
        float vx = 0;
        //        Vector2 transform = transformMapper.get(entity).position;
        Vector2 mouse = worldRend.getMouseWorldPos();
        //if (physicsMapper.get(entities.get(0)).onGround) {
        boolean up = InptMgr.isPressed(EnumInputIds.Up);
        boolean left = InptMgr.isPressed(EnumInputIds.Left);
        boolean down = InptMgr.isPressed(EnumInputIds.Down);
        boolean right = InptMgr.isPressed(EnumInputIds.Right);
        boolean backlayer = InptMgr.isPressed(EnumInputIds.BackLayerMod);
        TileLayer layer = backlayer ? TileLayer.Back : TileLayer.Front;
        if (InptMgr.isJustPressed(EnumInputIds.TestButton)) {
            healthMapper.get(entity).currentHealth -= backlayer ? -10 : 10;
        }
        if (up) {//&& solidGroundMapper.get(entity).isOnSolidGround()
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
        PhysicsComponent pc = physicsMapper.get(entity);
        pc.body.applyAccelerationW(vx * 3, vy * 3);
        pc.body.applyAccelerationPh(-pc.body.getLinearVelocityPh().x * 1.5f, -pc.body.getLinearVelocityPh().y * 1.5f);
        int hotbarChecked = checkSelectHotbarSlot(player.getInventory().getSelectedSlot());
        player.getInventory().setSelectedSlot(hotbarChecked);
        if (InptMgr.isPressed(EnumInputIds.TestExplodeTiles)) {
            int txm = Tile.toGlobalTile(mouse.x);
            int tym = Tile.toGlobalTile(mouse.y);
            final int rad = 3;
            for (int i = -rad; i <= rad; i++) {
                for (int j = -rad; j <= rad; j++) {
                    if (Mathf.square(i) + Mathf.square(j) <= Mathf.square(rad)) {
                        int tx = txm + i;
                        int ty = tym + j;
                        world.breakTile(tx, ty, layer, InstantBreaker.INSTANCE);
                    }
                }
            }
        }
        if (InptMgr.isPressed(EnumInputIds.BreakAttack)) {
            int tx = Tile.toGlobalTile(mouse.x);
            int ty = Tile.toGlobalTile(mouse.y);
            ItemStack stack = player.getInventory().getSelectedStack();
            boolean used = false;
            if (stack != null && stack.getItem() != null) {
                ItemStack cp = stack.cpy();
                used = stack.getItem().onItemAttack(player, cp, world, tx, ty, mouse.x, mouse.y);
                player.getInventory().setSlotContent(player.getInventory().getSelectedSlot(), cp);
            }
            if (!used) {
                world.breakTile(tx, ty, layer, br);
            }
        }
        if (InptMgr.isPressed(EnumInputIds.Use)) {
            //Current mouse stuff
            int tx = Tile.toGlobalTile(mouse.x);
            int ty = Tile.toGlobalTile(mouse.y);
            //get current item
            boolean used = false;
            ItemStack stack = player.getInventory().getSelectedStack();
            if (player.getInventory().getSelectedStack() != null
                    && !player.getInventory().getSelectedStack().isEmpty()) {
                //onItemUse
                if (stack != null && stack.getItem() != null) {
                    ItemStack cp = stack.cpy();
                    used = stack.getItem().onItemUse(player, cp, world, tx, ty, mouse.x, mouse.y, layer);//Hmmm... does the layer fit here?
                    player.getInventory().setSlotContent(player.getInventory().getSelectedSlot(), cp);
                }
            }
            if (!used) {
                Tile clicked = world.getTile(tx, ty, TileLayer.Front);//Only allow using the front layer... (afaik backlayer doesnt support tile entities?)
                //onTileUse
                ItemStack cp = stack != null ? stack.cpy() : null;
                used = clicked.onTileUse(player, world, cp, tx, ty);
                player.getInventory().setSlotContent(player.getInventory().getSelectedSlot(), cp);
            }
        }
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
