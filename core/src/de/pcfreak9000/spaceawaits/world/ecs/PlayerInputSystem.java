package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;
import de.pcfreak9000.spaceawaits.core.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.RenderLayers;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;
import de.pcfreak9000.spaceawaits.world.render.WorldScreen;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderRenderableComponent;

public class PlayerInputSystem extends EntitySystem {
    
    private final GameScreen worldRend;
    
    private Player player;
    
    private World world;
    
    private Entity tileSelectorEntity;
    
    public PlayerInputSystem(World world, GameScreen renderer) {
        this.worldRend = renderer;
        this.tileSelectorEntity = createTileSelectorEntity();
        this.world = world;
        world.getWorldBus().register(this);
    }
    
    @EventSubscription
    public void joined(WorldEvents.PlayerJoinedEvent ev) {
        this.player = ev.player;
    }
    
    @EventSubscription
    private void tsEntityHide(RendererEvents.OpenGuiOverlay ev) {
        Components.RENDER.get(tileSelectorEntity).enabled = false;
    }
    
    @EventSubscription
    private void tsEntityShow(RendererEvents.CloseGuiOverlay ev) {
        Components.RENDER.get(tileSelectorEntity).enabled = true;
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
        //How was that input multiplexing going again?! well it needs to be THIS way as other important things rely on InptMgr...
        boolean enableInput = !worldRend.isGuiContainerOpen();
        Entity entity = this.player.getPlayerEntity();
        Vector2 pos = Components.TRANSFORM.get(entity).position;
        for (ItemStack s : player.getDroppingQueue()) {
            s.drop(world, pos.x, pos.y);
        }
        player.getDroppingQueue().clear();
        
        PlayerInputComponent play = Components.PLAYER_INPUT.get(entity);
        float vy = 0;
        float vx = 0;
        //        Vector2 transform = transformMapper.get(entity).position;
        //if (physicsMapper.get(entities.get(0)).onGround) {
        boolean up = enableInput && InptMgr.isPressed(EnumInputIds.Up);
        boolean left = enableInput && InptMgr.isPressed(EnumInputIds.Left);
        boolean down = enableInput && InptMgr.isPressed(EnumInputIds.Down);
        boolean right = enableInput && InptMgr.isPressed(EnumInputIds.Right);
        boolean backlayer = enableInput && InptMgr.isPressed(EnumInputIds.BackLayerMod);
        boolean onSolidGround = Components.ON_SOLID_GROUND.get(entity).isOnSolidGround();
        boolean canmovefreely = Components.ON_SOLID_GROUND.get(entity).canMoveFreely();
        if (enableInput && InptMgr.isJustPressed(EnumInputIds.TestButton)) {
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
            if (enableInput && !InptMgr.isPressed(EnumInputIds.MovMod)) {
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
        if (enableInput && !InptMgr.isPressed(EnumInputIds.MovMod)) {
            int hotbarChecked = checkSelectHotbarSlot(player.getInventory().getSelectedSlot());
            player.getInventory().setSelectedSlot(hotbarChecked);
        } else if (enableInput) {
            float scroll = InptMgr.getScrollY() * 0.1f;
            ((WorldScreen) worldRend).changeZoom(scroll);
        }
        
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
    
    //TODO Have a dedicated system for context clues and the tileselectorentity?
    private Entity createTileSelectorEntity() {
        Entity e = new EntityImproved();
        RenderComponent rc = new RenderComponent(RenderLayers.WORLD_HUD);
        rc.considerAsGui = true;
        e.add(rc);
        RenderRenderableComponent tex = new RenderRenderableComponent();
        tex.renderable = CoreRes.TILEMARKER_DEF;
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
