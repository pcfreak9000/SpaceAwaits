package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.WorldEvents;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;
import de.pcfreak9000.spaceawaits.tileworld.tile.TileWorld;

public class PlayerInputSystem extends IteratingSystem {
    
    public PlayerInputSystem() {
        super(Family.all(PlayerInputComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    private final ComponentMapper<PlayerInputComponent> mapper = ComponentMapper.getFor(PlayerInputComponent.class);
    private final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    
    private TileWorld world;
    private Camera cam;
    
    @EventSubscription
    public void settwevent(WorldEvents.SetWorldEvent ev) {
        this.world = ev.getTileWorldNew();
        this.cam = ev.worldMgr.getRenderInfo().getCamera();
    }
    
    private Tile ugly = null;

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
  
        PlayerInputComponent play = this.mapper.get(entity);
        float vy = 0;
        float vx = 0;
        //if (physicsMapper.get(entities.get(0)).onGround) {
        if (Keys.FORWARD.isPressed() || Keys.UP.isPressed()) {
            vy += play.maxYv * 5;
        }
        //kinda useless, use for sneaking/ladders instead?
        if (Keys.BACKWARD.isPressed() || Keys.DOWN.isPressed()) {
            vy -= play.maxYv * 5;
        }
        //}
        if (Keys.LEFT.isPressed()) {
            vx -= play.maxXv * 5;
        }
        if (Keys.RIGHT.isPressed()) {
            vx += play.maxXv * 5;
        }
        if (Keys.SHOOT.isPressed()) {
            Vector2f mouse = Omnikryptec.getInput().getMousePositionInWorld2D(this.cam, new Vector2f());
            PhysicsComponent pc = this.physicsMapper.get(entity);
            float delx = mouse.x - (pc.x + pc.w / 2);
            float dely = mouse.y - (pc.y + pc.h / 2);
            Vector2f vec = new Vector2f(delx, dely).normalize();
            Entity ent = new Entity();
            PhysicsComponent epc = new PhysicsComponent();
            epc.velocity.set(vec).mul(150);
            epc.w = 10;
            epc.h = 10;
            epc.restitution = 0.9f;
            Sprite sprite = new Sprite();
            //AdvancedSprite sprite = new AdvancedSprite();
            sprite.getTransform().localspaceWrite().scale(10);
            sprite.getRenderData().setUVAndTexture(Omnikryptec.getTexturesS().get("sdfgsdfsdf"));
            sprite.setLayer(50);
            RenderComponent rendComp = new RenderComponent(sprite);
            ent.addComponent(rendComp);
            ent.addComponent(epc);
            TransformComponent trans = new TransformComponent();
            trans.transform.localspaceWrite().setTranslation(pc.x + pc.w / 2, pc.y + pc.h / 2);
            ent.addComponent(trans);
            iecsManager.addEntity(ent);
        }
        this.physicsMapper.get(entity).acceleration.set(vx * 3, vy * 3 - 98.1f);
        if (Keys.EXPLODE_DEBUG.isPressed()) {
            Vector2f mouse = Omnikryptec.getInput().getMousePositionInWorld2D(this.cam, new Vector2f());
            int txm = Tile.toGlobalTile(mouse.x());
            int tym = Tile.toGlobalTile(mouse.y());
            final int rad = 3;
            for (int i = -rad; i <= rad; i++) {
                for (int j = -rad; j <= rad; j++) {
                    if (Mathf.square(i) + Mathf.square(j) <= Mathf.square(rad)) {
                        int tx = txm + i;
                        int ty = tym + j;
                        Tile t = world.getTile(tx, ty);
                        if (t != null && t.canBreak()) {
                            this.ugly = t;
                            world.setTile(Tile.EMPTY, tx, ty);
                        }
                        
                    }
                }
            }
        }
        if (Keys.DESTROY.isPressed()) {
            Vector2f mouse = Omnikryptec.getInput().getMousePositionInWorld2D(this.cam, new Vector2f());
            int tx = Tile.toGlobalTile(mouse.x());
            int ty = Tile.toGlobalTile(mouse.y());
            Region r = this.world.requestRegion(Region.toGlobalRegion(tx), Region.toGlobalRegion(ty));
            if (r != null) {
                Tile t = r.getTile(tx, ty);
                if (t != null && t.canBreak()) {
                    this.ugly = t;
                    r.setTile(Tile.EMPTY, tx, ty);
                }
            }
        }
        if (Keys.BUILD.isPressed()) {
            Vector2f mouse = Omnikryptec.getInput().getMousePositionInWorld2D(this.cam, new Vector2f());
            int tx = Tile.toGlobalTile(mouse.x());
            int ty = Tile.toGlobalTile(mouse.y());
            Region r = this.world.requestRegion(Region.toGlobalRegion(tx), Region.toGlobalRegion(ty));
            if (r != null && this.ugly != null) {
                if (r.getTile(tx, ty) == null || r.getTile(tx, ty) == Tile.EMPTY) {
                    r.setTile(this.ugly, tx, ty);
                }
            }
        }
        PhysicsComponent pc = physicsMapper.get(entity);
        pc.acceleration.sub(pc.velocity.x() * 1.5f, pc.velocity.y() * 1.5f, pc.acceleration);
    }

    
}
