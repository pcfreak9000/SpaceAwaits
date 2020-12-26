package de.pcfreak9000.spaceawaits.tileworld.ecs;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.MathUtil;
import de.omnikryptec.math.Mathf;
import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.WorldEvents;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;
import de.pcfreak9000.spaceawaits.tileworld.tile.TileState;
import de.pcfreak9000.spaceawaits.tileworld.tile.TileWorld;

public class PhysicsSystem extends IteratingSystem {
    
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    private final ComponentMapper<PhysicsComponent> physicsMapper = ComponentMapper.getFor(PhysicsComponent.class);
    
    private static final float STEPSIZE_SECONDS = 1 / 100f;
    
    private TileWorld tileWorld;
    private float deltaAcc = 0;
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.tileWorld = svwe.getTileWorldNew();
    }
    
    public PhysicsSystem() {
        super(Family.all(PhysicsComponent.class, TransformComponent.class).get());
        SpaceAwaits.BUS.register(this);
    }
    
    @Override
    public void update(float deltat) {
        this.deltaAcc += deltat;
        if (deltaAcc > 10 * STEPSIZE_SECONDS) {
            Logger.getLogger(PhysicsSystem.class).warnf("Skipping physics ticks, acc. physics time: %f", deltaAcc);
            deltaAcc = 10 * STEPSIZE_SECONDS;
        }
        while (deltaAcc >= STEPSIZE_SECONDS) {
            deltaAcc -= STEPSIZE_SECONDS;
            for (Entity e : getEntities()) {
                processEntity(e, deltat);
            }
        }
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        
        TransformComponent tc = this.transformMapper.get(entity);
        PhysicsComponent pc = this.physicsMapper.get(entity);
        
        Vector2 positionState = tc.position;
        
        //Friction TODO manage elsewhere
        //pc.acceleration.sub(pc.velocity.x() * 1.5f, pc.velocity.y() * 1.5f, pc.acceleration);
        //Integrate motion
        float posDeltaX = 0.5f * pc.acceleration.x * Mathf.square(STEPSIZE_SECONDS) + pc.velocity.x * STEPSIZE_SECONDS;
        float posDeltaY = 0.5f * pc.acceleration.y * Mathf.square(STEPSIZE_SECONDS) + pc.velocity.y * STEPSIZE_SECONDS;
        pc.velocity.add(pc.acceleration.x * STEPSIZE_SECONDS, pc.acceleration.y * STEPSIZE_SECONDS);
        
        //Check and resolve collisions
        if (pc.w != 0 || pc.h != 0) {
            pc.onGround = false;
            float tRemaining = 1.0f;
            TileState tile = null;
            for (int i = 0; i < 10 && tRemaining > 0.0f; i++) {//10? make variable depending on colliding object
                float tMin = 1.0f;
                positionState = tc.position;
                pc.x = positionState.x;//TODO implement offset?
                pc.y = positionState.y;
                //pc.onGround = false;
                List<TileState> collisions = new ArrayList<>();
                //Collect possible tile collisions
                this.tileWorld.collectTileIntersections(collisions, -1 + (int) Mathf.floor(pc.x / Tile.TILE_SIZE),
                        -1 + (int) Mathf.floor(pc.y / Tile.TILE_SIZE),
                        1 + (int) Mathf.ceil((pc.w + posDeltaX) / Tile.TILE_SIZE),
                        1 + (int) Mathf.ceil((pc.h + posDeltaY) / Tile.TILE_SIZE), (t) -> t.getTile().isSolid());
                for (TileState t : collisions) {
                    Vector2 result = new Vector2();
                    //Minkowski sum used
                    if (MathUtil.intersectRayAab(pc.x + pc.w / 2, pc.y + pc.h / 2, 0, posDeltaX, posDeltaY, 0,
                            t.getGlobalTileX() * Tile.TILE_SIZE - pc.w / 2 - 0.0001f,
                            t.getGlobalTileY() * Tile.TILE_SIZE - pc.h / 2 - 0.0001f, 0,
                            (1 + t.getGlobalTileX()) * Tile.TILE_SIZE + pc.w / 2 + 0.0001f,
                            (1 + t.getGlobalTileY()) * Tile.TILE_SIZE + pc.h / 2 + 0.0001f, 0, result)) {
                        if (result.x >= 0) {
                            if (result.x < tMin) {
                                tMin = result.x;
                                tile = t;
                            }
                            if (tMin == 0) {
                                //Cant get a closer collision
                                break;
                            }
                        }
                    }
                }
                float tMinActual = tMin;
                if (tMin < 1.0f) {
                    //Not useful? Useful? oof
                    tMin *= 0.9999f;
                }
                tc.position.set(positionState.x + posDeltaX * tMin, positionState.y + posDeltaY * tMin);
                if (tMin < 1.0f) {
                    Vector2 normal = new Vector2();
                    float pen = getNormal(tile, pc, posDeltaX, posDeltaY, tMinActual, normal);
                    pc.onGround |= normal.y == 1f;//<- does that work correctly?
                    //Epsilon allows for sliding and makes stuff not so sticky
                    float bouncynessFactor = 1.0001f + Mathf.max(tile.getTile().getBouncyness(), pc.restitution);
                    pc.velocity.sub(new Vector2(normal).scl(bouncynessFactor * pc.velocity.dot(normal)));
                    Vector2 hehe = new Vector2(posDeltaX, posDeltaY);
                    hehe.sub(new Vector2(normal).scl(bouncynessFactor * hehe.dot(normal)));
                    posDeltaX = hehe.x;
                    posDeltaY = hehe.y;
                    //Positional correction because floating point error (fixes gliding through the tiles on y=1 or y=0 (y>1 not affected for some reason)
                    tc.position.add(normal.scl(pen * 0.3f));
                }
                tRemaining -= tMin * tRemaining;
            }
        } else {
            tc.position.set(positionState.x + posDeltaX, positionState.y + posDeltaY);
        }
        
    }
    
    private float getNormal(TileState t, PhysicsComponent pc, float posDelX, float posDelY, float tMin,
            Vector2 normal) {
        float woverlap = -1;
        float hoverlap = -1;
        float projectedX = pc.x + posDelX * tMin;
        float projectedY = pc.y + posDelY * tMin;
        if (projectedX > t.getGlobalTileX() * Tile.TILE_SIZE) {
            woverlap = (t.getGlobalTileX() + 1.0f) * Tile.TILE_SIZE - projectedX;
        } else {
            woverlap = projectedX + pc.w - t.getGlobalTileX() * Tile.TILE_SIZE;
        }
        if (projectedY > t.getGlobalTileY() * Tile.TILE_SIZE) {
            hoverlap = (t.getGlobalTileY() + 1.0f) * Tile.TILE_SIZE - projectedY;
        } else {
            hoverlap = projectedY + pc.h - t.getGlobalTileY() * Tile.TILE_SIZE;
        }
        //Stupid epsilon stuff
        woverlap += 0.001f;
        hoverlap += 0.001f;
        if (woverlap >= 0 && hoverlap >= 0) {
            woverlap = Mathf.min(woverlap, Tile.TILE_SIZE);
            hoverlap = Mathf.min(hoverlap, Tile.TILE_SIZE);
            float wperc = woverlap / Tile.TILE_SIZE;
            float hperc = hoverlap / Tile.TILE_SIZE;
            if (wperc < hperc) {
                normal.set(new Vector2(Math.signum(projectedX - t.getGlobalTileX() * Tile.TILE_SIZE), 0));
                return woverlap;
            } else {
                normal.set(new Vector2(0, Math.signum(projectedY - t.getGlobalTileY() * Tile.TILE_SIZE)));
                return hoverlap;
            }
        }
        throw new IllegalStateException("Negative overlap");
    }
    
}
