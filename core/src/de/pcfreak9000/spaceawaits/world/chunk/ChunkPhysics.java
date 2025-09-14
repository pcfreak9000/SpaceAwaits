package de.pcfreak9000.spaceawaits.world.chunk;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.box2d.enums.b2BodyType;
import com.badlogic.gdx.box2d.structs.b2BodyDef;
import com.badlogic.gdx.box2d.structs.b2BodyId;
import com.badlogic.gdx.box2d.structs.b2Polygon;
import com.badlogic.gdx.box2d.structs.b2Rot;
import com.badlogic.gdx.box2d.structs.b2ShapeDef;
import com.badlogic.gdx.box2d.structs.b2ShapeId;
import com.badlogic.gdx.box2d.structs.b2SurfaceMaterial;
import com.badlogic.gdx.box2d.structs.b2Vec2;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.world.physics.IDFactory;
import de.pcfreak9000.spaceawaits.world.physics.ecs.IBodyFactory;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class ChunkPhysics implements IBodyFactory {
    
    private static final Vector2 BODY_OFFSET = new Vector2(0.5f, 0.5f);
    
    private final Chunk chunk;
    private b2BodyId body;
    
    public ChunkPhysics(Chunk chunk) {
        this.chunk = chunk;
        this.chunk.addListener(new ListenerClass());
    }
    
    @Override
    public b2BodyId createBody(b2WorldId world, Entity entity) {
        b2BodyDef bd = Box2d.b2DefaultBodyDef();
        b2Vec2 pos = bd.position();
        pos.x(METER_CONV.in(chunk.getGlobalTileX() + 0.5f));
        pos.y(METER_CONV.in(chunk.getGlobalTileY() + 0.5f));
        bd.type(b2BodyType.b2_staticBody);
        b2BodyId b = Box2d.b2CreateBody(world, bd.asPointer());
        this.body = b;
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
                int x = chunk.getGlobalTileX() + i;
                int y = chunk.getGlobalTileY() + j;
                Tile t = chunk.getTile(x, y, TileLayer.Front);
                Tile top = j + 1 >= Chunk.CHUNK_SIZE ? null : chunk.getTile(x, y + 1, TileLayer.Front);
                Tile bot = j - 1 < 0 ? null : chunk.getTile(x, y - 1, TileLayer.Front);
                Tile right = i + 1 >= Chunk.CHUNK_SIZE ? null : chunk.getTile(x + 1, y, TileLayer.Front);
                Tile left = i - 1 < 0 ? null : chunk.getTile(x - 1, y, TileLayer.Front);
                if ((top == null || !isSolidAndFilling(top)) || (bot == null || !isSolidAndFilling(bot))
                        || (right == null || !isSolidAndFilling(right)) || (left == null || !isSolidAndFilling(left))
                        || t.hasCustomHitbox()) {
                    if (t.isSolid() || t.getContactListener() != null) {
                        createFixture(t, x, y);
                    }
                }
            }
        }
        Box2d.b2Body_SetUserData(b, IDFactory.putData(this));
        //b.setUserData(new UserData(false));
        return b;
    }
    
    @Override
    public void destroyBody(b2BodyId body, b2WorldId world) {
        for (int i = 0; i < Chunk.CHUNK_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_SIZE; j++) {
                int x = chunk.getGlobalTileX() + i;
                int y = chunk.getGlobalTileY() + j;
                TileState state = chunk.getTileState(x, y);
                state.setFixture(null);
            }
        }
        this.body = null;
        IBodyFactory.super.destroyBody(body, world);
    }
    
    private void createFixture(Tile tile, int gtx, int gty) { //This could be more memory efficient
        b2ShapeDef fd = Box2d.b2DefaultShapeDef();
        b2SurfaceMaterial mat = Box2d.b2DefaultSurfaceMaterial();
        b2Polygon shape;
        mat.friction(1f);
        mat.restitution(tile.getBouncyness());
        fd.setMaterial(mat);
        if (!tile.hasCustomHitbox()||true) {
        	b2Vec2 offset = new b2Vec2();
        	offset.x(METER_CONV.in((gtx - chunk.getGlobalTileX())));
        	offset.y(METER_CONV.in((gty - chunk.getGlobalTileY())));
        	b2Rot rot = Box2d.b2MakeRot(0);
        	shape = Box2d.b2MakeOffsetBox(METER_CONV.in(0.5f), METER_CONV.in(0.5f), offset, rot);
        } else {
            float[] custom = tile.getCustomHitbox();
            for (int i = 0; i < custom.length; i++) {
                if (i % 2 == 0) {//x comp
                    custom[i] = METER_CONV.in(gtx - chunk.getGlobalTileX() + custom[i] - 0.5f);
                } else {//y comp
                    custom[i] = METER_CONV.in(gty - chunk.getGlobalTileY() + custom[i] - 0.5f);
                }
            }
            Box2d.b2ComputeHull(null, gty);
            //shape.set(custom);
        }
        if (!tile.isSolid() && tile.getContactListener() != null) {
            fd.isSensor(true);
        }
        b2ShapeId fix = Box2d.b2CreatePolygonShape(body, fd.asPointer(), shape.asPointer());
        chunk.getTileState(gtx, gty).setFixture(fix);
        Box2d.b2Shape_SetUserData(fix, IDFactory.putData(tile));//Enough for now
    }
    
    public boolean isSolidAndFilling(Tile t) {
        return t.isSolid() && !t.hasCustomHitbox();
    }
    
    private final class ListenerClass implements ChunkChangeListener {
        @Override
        public void onTileStateChange(Chunk chunk, TileState state, Tile newTile, Tile oldTile, int gtx, int gty,
                TileLayer tilelayer) {
            if (tilelayer == TileLayer.Back) {
                return;
            }
            if (body == null) {
                //The chunk hasn't been added to the simulation yet
                return;
            }
            if (isSolidAndFilling(oldTile) != isSolidAndFilling(newTile)) {
                if (!newTile.isSolid()) { //oldstate was solid, newstate isn't
                    if (state.getFixture() != null) { //the fixture is null if oldstate was surrounded by solid tiles
                        destroyFixture(state);
                    }
                    for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {
                        int x = gtx + d.dx;
                        int y = gty + d.dy;
                        TileState ts = chunk.getTileStateSafe(x, y);
                        if (ts != null && ts.getTile().isSolid() && ts.getFixture() == null) {
                            createFixture(ts.getTile(), x, y);
                        }
                    }
                    if (newTile.getContactListener() != null) {
                        createFixture(newTile, gtx, gty);
                    }
                } else { //newstate is solid, oldstate wasn't
                    if (oldTile.getContactListener() != null) {
                        destroyFixture(state);
                    }
                    boolean createFix = newTile.hasCustomHitbox();
                    for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {
                        if (createFix) {
                            break;
                        }
                        int x = gtx + d.dx;
                        int y = gty + d.dy;
                        TileState ts = chunk.getTileStateSafe(x, y);
                        createFix |= (ts == null || !isSolidAndFilling(ts.getTile()));
                    }
                    if (createFix) {
                        createFixture(newTile, gtx, gty);
                        for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {
                            int x = gtx + d.dx;
                            int y = gty + d.dy;
                            checkDestroy(chunk.getTileStateSafe(x, y), x, y);
                        }
                    }
                }
            } else {
                //new and old are both solid and filling or both not solid and filling. The fixture is retained if both are solid and filling. 
                //The restitution might have to change
                boolean normalTileFilled = isSolidAndFilling(oldTile) && isSolidAndFilling(newTile);
                if (normalTileFilled && state.getFixture() != null) {
                	Box2d.b2Shape_SetRestitution(state.getFixture(), newTile.getBouncyness());
                } else if (!normalTileFilled) {//deal with custom hitboxes. In this case doesn't change the state of surrounding fixtures, so this is relatively simple.
                    if (state.getFixture() != null) {
                        destroyFixture(state);
                    }
                    if (newTile.hasCustomHitbox() || newTile.getContactListener() != null) {
                        createFixture(newTile, gtx, gty);
                    }
                }
            }
        }
        
        private void checkDestroy(TileState state, int tx, int ty) {
            if (state != null && state.getTile().isSolid()) {
                int topty = ty + 1;
                int botty = ty - 1;
                int righttx = tx + 1;
                int lefttx = tx - 1;
                Tile bot = chunk.inBounds(tx, botty) ? chunk.getTile(tx, botty, TileLayer.Front) : null;
                Tile top = chunk.inBounds(tx, topty) ? chunk.getTile(tx, topty, TileLayer.Front) : null;
                Tile right = chunk.inBounds(righttx, ty) ? chunk.getTile(righttx, ty, TileLayer.Front) : null;
                Tile left = chunk.inBounds(lefttx, ty) ? chunk.getTile(lefttx, ty, TileLayer.Front) : null;
                if ((top != null && isSolidAndFilling(top)) && (right != null && isSolidAndFilling(right))
                        && (left != null && isSolidAndFilling(left)) && (bot != null && isSolidAndFilling(bot))) {
                    destroyFixture(state);
                }
            }
        }
        
        private void destroyFixture(TileState state) {
        	Box2d.b2DestroyShape(state.getFixture(), true);
            state.setFixture(null);
        }
        
    }
    
    @Override
    public Vector2 bodyOffset() {
        return BODY_OFFSET;
    }
    
    @Override
    public Vector2 boundingBoxWidthAndHeight() {
        return null;
    }
    
}
