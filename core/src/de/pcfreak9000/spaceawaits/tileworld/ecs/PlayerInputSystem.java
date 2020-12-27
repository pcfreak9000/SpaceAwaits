package de.pcfreak9000.spaceawaits.tileworld.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

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
    
    @EventSubscription
    public void settwevent(WorldEvents.SetWorldEvent ev) {
        this.world = ev.getTileWorldNew();
    }
    
    private Tile ugly = null;
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        
        PlayerInputComponent play = this.mapper.get(entity);
        float vy = 0;
        float vx = 0;
        //if (physicsMapper.get(entities.get(0)).onGround) {
        boolean up = Gdx.input.isKeyPressed(Keys.W);
        boolean left = Gdx.input.isKeyPressed(Keys.A);
        boolean down = Gdx.input.isKeyPressed(Keys.S);
        boolean right = Gdx.input.isKeyPressed(Keys.D);
        boolean explode = Gdx.input.isButtonPressed(Buttons.MIDDLE);
        boolean destroy = Gdx.input.isButtonPressed(Buttons.LEFT);
        boolean build = Gdx.input.isButtonPressed(Buttons.RIGHT);
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
        //        if (Keys.SHOOT.isPressed()) {
        //            Vector2f mouse = Omnikryptec.getInput().getMousePositionInWorld2D(this.cam, new Vector2f());
        //            PhysicsComponent pc = this.physicsMapper.get(entity);
        //            float delx = mouse.x - (pc.x + pc.w / 2);
        //            float dely = mouse.y - (pc.y + pc.h / 2);
        //            Vector2f vec = new Vector2f(delx, dely).normalize();
        //            Entity ent = new Entity();
        //            PhysicsComponent epc = new PhysicsComponent();
        //            epc.velocity.set(vec).mul(150);
        //            epc.w = 10;
        //            epc.h = 10;
        //            epc.restitution = 0.9f;
        //            Sprite sprite = new Sprite();
        //            //AdvancedSprite sprite = new AdvancedSprite();
        //            sprite.getTransform().localspaceWrite().scale(10);
        //            sprite.getRenderData().setUVAndTexture(Omnikryptec.getTexturesS().get("sdfgsdfsdf"));
        //            sprite.setLayer(50);
        //            RenderComponent rendComp = new RenderComponent(sprite);
        //            ent.addComponent(rendComp);
        //            ent.addComponent(epc);
        //            TransformComponent trans = new TransformComponent();
        //            trans.transform.localspaceWrite().setTranslation(pc.x + pc.w / 2, pc.y + pc.h / 2);
        //            ent.addComponent(trans);
        //            iecsManager.addEntity(ent);
        //        }
        this.physicsMapper.get(entity).acceleration.set(vx * 3, vy * 3 - 98.1f);
        if (explode) {
            //TODO Well that is ugly...
            Vector2 mouse = SpaceAwaits.getSpaceAwaits().getWorldManager().getRenderInfo().getViewport()
                    .unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            int txm = Tile.toGlobalTile(mouse.x);
            int tym = Tile.toGlobalTile(mouse.y);
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
        if (destroy) {
            Vector2 mouse = SpaceAwaits.getSpaceAwaits().getWorldManager().getRenderInfo().getViewport()
                    .unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            int tx = Tile.toGlobalTile(mouse.x);
            int ty = Tile.toGlobalTile(mouse.y);
            Tile t = world.getTile(tx, ty);
            if (t != null && t.canBreak()) {
                this.ugly = t;
                world.setTile(Tile.EMPTY, tx, ty);
            }
            
        }
        if (build) {
            Vector2 mouse = SpaceAwaits.getSpaceAwaits().getWorldManager().getRenderInfo().getViewport()
                    .unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            int tx = Tile.toGlobalTile(mouse.x);
            int ty = Tile.toGlobalTile(mouse.y);
            if (this.ugly != null) {
                if (world.getTile(tx, ty) == null || world.getTile(tx, ty) == Tile.EMPTY) {
                    world.setTile(this.ugly, tx, ty);
                }
            }
        }
        PhysicsComponent pc = physicsMapper.get(entity);
        pc.acceleration.sub(pc.velocity.x * 1.5f, pc.velocity.y * 1.5f);
    }
    
}
