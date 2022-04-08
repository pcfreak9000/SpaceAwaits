package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.RenderLayers;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderTextureComponent;

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
    
    private Entity tileSelectorEntity;
    
    public PlayerInputSystem(World world, GameRenderer renderer) {
        this.world = world;
        this.worldRend = renderer;
        this.tileSelectorEntity = createTileSelectorEntity();
        world.getWorldBus().register(this);
    }
    
    @EventSubscription
    public void joined(WorldEvents.PlayerJoinedEvent ev) {
        if (ev.world == this.world) {//TODO world specific eventbus?
            this.player = ev.player;
        }
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntity(tileSelectorEntity);
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntity(tileSelectorEntity);
    }
    
    @Override
    public void update(float deltaTime) {
        if (this.player == null) {
            return;
        }
        if (worldRend.isGuiContainerOpen()) {
            return;
        }
        
        Entity entity = this.player.getPlayerEntity();
        PlayerInputComponent play = mapper.get(entity);
        float vy = 0;
        float vx = 0;
        //        Vector2 transform = transformMapper.get(entity).position;
        //if (physicsMapper.get(entities.get(0)).onGround) {
        boolean up = InptMgr.isPressed(EnumInputIds.Up);
        boolean left = InptMgr.isPressed(EnumInputIds.Left);
        boolean down = InptMgr.isPressed(EnumInputIds.Down);
        boolean right = InptMgr.isPressed(EnumInputIds.Right);
        boolean backlayer = InptMgr.isPressed(EnumInputIds.BackLayerMod);
        boolean onSolidGround = solidGroundMapper.get(entity).isOnSolidGround();
        if (InptMgr.isJustPressed(EnumInputIds.TestButton)) {
            healthMapper.get(entity).currentHealth -= backlayer ? -10 : 10;
        }
        if (onSolidGround) {
            if (up) {
                vy += play.maxYv * 5;
                
            }
            //kinda useless, use for sneaking/ladders instead?
            if (down) {
                vy -= play.maxYv * 5;
            }
        }
        if (left) {
            vx -= play.maxXv;
        }
        if (right) {
            vx += play.maxXv;
        }
        PhysicsComponent pc = physicsMapper.get(entity);
        pc.body.applyAccelerationW(vx * 6, vy * 3);
        pc.body.applyAccelerationPh(-pc.body.getLinearVelocityPh().x * 40, -pc.body.getLinearVelocityPh().y * 0.1f);
        int hotbarChecked = checkSelectHotbarSlot(player.getInventory().getSelectedSlot());
        player.getInventory().setSelectedSlot(hotbarChecked);
    }
    
    private int checkSelectHotbarSlot(int current) {
        for (int i = Keys.NUM_1; i <= Keys.NUM_9; i++) {
            if (Gdx.input.isKeyPressed(i)) {
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
    
    private Entity createTileSelectorEntity() {
        Entity e = new EntityImproved();
        RenderComponent rc = new RenderComponent(RenderLayers.WORLD_HUD, "entity");
        rc.considerAsGui = true;
        e.add(rc);
        RenderTextureComponent tex = new RenderTextureComponent();
        tex.texture = CoreRes.TILEMARKER_DEF;
        tex.width = 1;
        tex.height = 1;
        tex.color = Color.GRAY;
        e.add(tex);
        e.add(new TransformComponent());
        FollowMouseComponent fmc = new FollowMouseComponent();
        fmc.tiled = true;
        e.add(fmc);
        return e;
    }
}
