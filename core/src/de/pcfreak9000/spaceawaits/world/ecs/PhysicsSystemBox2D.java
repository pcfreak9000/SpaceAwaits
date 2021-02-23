package de.pcfreak9000.spaceawaits.world.ecs;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.world.UnitConversion;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.WorldEvents.ChunkLoadedEvent;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class PhysicsSystemBox2D extends IteratingSystem implements EntityListener {
    
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    private final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    
    private static final float STEPSIZE_SECONDS = 1 / 60f;
    private static final float PIXELS_PER_METER = Tile.TILE_SIZE * 1.5f;
    public static final UnitConversion METER_CONV = new UnitConversion(PIXELS_PER_METER);//TODO not public?
    
    private float deltaAcc = 0;
    
    private World bworld;
    
    private Box2DDebugRenderer debugRend;
    private Camera cam;
    private boolean debug = true;
    
    public PhysicsSystemBox2D() {
        super(Family.all(PhysicsComponent.class, TransformComponent.class).get());
        SpaceAwaits.BUS.register(this);
        this.bworld = new World(new Vector2(0, -9.81f), true);
        this.bworld.setAutoClearForces(false);
        this.debugRend = new Box2DDebugRenderer(true, true, true, true, true, true);
    }
    
    @EventSubscription
    private void ev(WorldEvents.SetWorldEvent ev) {
        this.cam = ev.worldMgr.getRenderInfo().getCamera();
    }
    
    @EventSubscription
    private void chunkloadedev(ChunkLoadedEvent ev) {
        BodyDef bd = new BodyDef();
        FixtureDef fd = new FixtureDef();
        bd.position.set(METER_CONV.in(ev.chunk.getGlobalTileX() * Tile.TILE_SIZE + 0.5f * Tile.TILE_SIZE),
                METER_CONV.in(ev.chunk.getGlobalTileY() * Tile.TILE_SIZE + 0.5f * Tile.TILE_SIZE));
        bd.type = BodyType.StaticBody;
        Body b = this.bworld.createBody(bd);
        PolygonShape shape = new PolygonShape();
        fd.shape = shape;
        //TMP
        for (int i = 0; i < Chunk.CHUNK_TILE_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_TILE_SIZE; j++) {
                int x = ev.chunk.getGlobalTileX() + i;
                int y = ev.chunk.getGlobalTileY() + j;
                Tile t = ev.chunk.getTile(x, y);
                Tile top = j + 1 >= Chunk.CHUNK_TILE_SIZE ? null : ev.chunk.getTile(x, y + 1);
                Tile bot = j - 1 < 0 ? null : ev.chunk.getTile(x, y - 1);
                Tile right = i + 1 >= Chunk.CHUNK_TILE_SIZE ? null : ev.chunk.getTile(x + 1, y);
                Tile left = i - 1 < 0 ? null : ev.chunk.getTile(x - 1, y);
                if ((top == null || !top.isSolid()) || (bot == null || !bot.isSolid())
                        || (right == null || !right.isSolid()) || (left == null || !left.isSolid())) {
                    if (t.isSolid()) {
                        shape.setAsBox(METER_CONV.in(Tile.TILE_SIZE / 2), METER_CONV.in(Tile.TILE_SIZE / 2),
                                new Vector2(METER_CONV.in(i * Tile.TILE_SIZE), METER_CONV.in(j * Tile.TILE_SIZE)), 0);
                        Fixture fix = b.createFixture(fd);
                        fix.setUserData(t);
                        ev.chunk.getTileState(x, y).setFixture(fix);
                    }
                }
            }
        }
        shape.dispose();
    }
    
    public void renderDebug() {
        if (debug) {
            OrthographicCamera cam = new OrthographicCamera();
            cam.setToOrtho(false, METER_CONV.in(this.cam.viewportWidth), METER_CONV.in(this.cam.viewportHeight));
            cam.position.set(METER_CONV.in(this.cam.position.x), METER_CONV.in(this.cam.position.y), 0);
            cam.update();
            debugRend.render(bworld, cam.combined);
        }
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntityListener(getFamily(), this);
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntityListener(this);
    }
    
    @Override
    public void update(float deltat) {
        this.deltaAcc += deltat;
        if (deltaAcc > 10 * STEPSIZE_SECONDS) {
            Logger.getLogger(PhysicsSystemBox2D.class).warnf("Skipping physics ticks, acc. physics time: %f", deltaAcc);
            deltaAcc = 10 * STEPSIZE_SECONDS;
        }
        for (Entity e : getEntities()) {
            pre(e);
            processEntity(e, deltat);
        }
        while (deltaAcc >= STEPSIZE_SECONDS) {
            deltaAcc -= STEPSIZE_SECONDS;
            this.bworld.step(STEPSIZE_SECONDS, 6, 2);
        }
        this.bworld.clearForces();
        for (Entity e : getEntities()) {
            post(e);
        }
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PhysicsComponent pc = this.physicsMapper.get(entity);
        pc.body.applyForceToCenter(METER_CONV.in(pc.acceleration.x) * pc.body.getMass(),
                METER_CONV.in(pc.acceleration.y) * pc.body.getMass(), true);
    }
    
    private void pre(Entity entity) {
        PhysicsComponent pc = this.physicsMapper.get(entity);
        TransformComponent tc = this.transformMapper.get(entity);
        pc.body.setTransform(METER_CONV.in(tc.position.x + pc.w / 2), METER_CONV.in(tc.position.y + pc.h / 2), 0);
    }
    
    private void post(Entity entity) {
        TransformComponent tc = this.transformMapper.get(entity);
        PhysicsComponent pc = this.physicsMapper.get(entity);
        pc.x = METER_CONV.out(pc.body.getPosition().x) - pc.w / 2;
        pc.y = METER_CONV.out(pc.body.getPosition().y) - pc.h / 2;
        tc.position.set(pc.x, pc.y);
        Vector2 v = pc.body.getLinearVelocity();
        pc.velocity.set(METER_CONV.out(v.x), METER_CONV.out(v.y));
    }
    
    @Override
    public void entityAdded(Entity entity) {
        PhysicsComponent pc = this.physicsMapper.get(entity);
        TransformComponent tc = this.transformMapper.get(entity);
        if (pc.w != 0 || pc.h != 0) {
            if (pc.body == null) {
                BodyDef bd = new BodyDef();
                bd.fixedRotation = true;
                bd.type = BodyType.DynamicBody;
                bd.position.set(METER_CONV.in(tc.position.x), METER_CONV.in(tc.position.y));
                bd.position.add(METER_CONV.in(pc.w / 2), METER_CONV.in(pc.h / 2));
                FixtureDef fd = new FixtureDef();
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(METER_CONV.in(pc.w / 2), METER_CONV.in(pc.h / 2));
                fd.shape = shape;
                Body b = bworld.createBody(bd);
                b.createFixture(fd).setUserData(pc);
                pc.body = b;
                shape.dispose();
            }
            pc.x = tc.position.x;
            pc.y = tc.position.y;
        }
    }
    
    @Override
    public void entityRemoved(Entity entity) {
        PhysicsComponent pc = this.physicsMapper.get(entity);
        if (pc.w != 0 || pc.h != 0) {
            bworld.destroyBody(pc.body);
            pc.body = null;
        }
    }
}
