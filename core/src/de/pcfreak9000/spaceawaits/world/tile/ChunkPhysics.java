package de.pcfreak9000.spaceawaits.world.tile;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.pcfreak9000.spaceawaits.world.physics.BodyFactory;

public class ChunkPhysics implements BodyFactory {
    
    private static final Vector2 BODY_OFFSET = new Vector2(0.5f * Tile.TILE_SIZE, 0.5f * Tile.TILE_SIZE);
    
    private final Chunk chunk;
    private Body body;
    
    public ChunkPhysics(Chunk chunk) {
        this.chunk = chunk;
        this.chunk.addListener(new ListenerClass());
    }
    
    @Override
    public Body createBody(World world) {
        BodyDef bd = new BodyDef();
        bd.position.set(METER_CONV.in(chunk.getGlobalTileX() * Tile.TILE_SIZE + 0.5f * Tile.TILE_SIZE),
                METER_CONV.in(chunk.getGlobalTileY() * Tile.TILE_SIZE + 0.5f * Tile.TILE_SIZE));
        bd.type = BodyType.StaticBody;
        Body b = world.createBody(bd);
        this.body = b;
        for (int i = 0; i < Chunk.CHUNK_TILE_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_TILE_SIZE; j++) {
                int x = chunk.getGlobalTileX() + i;
                int y = chunk.getGlobalTileY() + j;
                Tile t = chunk.getTile(x, y);
                Tile top = j + 1 >= Chunk.CHUNK_TILE_SIZE ? null : chunk.getTile(x, y + 1);
                Tile bot = j - 1 < 0 ? null : chunk.getTile(x, y - 1);
                Tile right = i + 1 >= Chunk.CHUNK_TILE_SIZE ? null : chunk.getTile(x + 1, y);
                Tile left = i - 1 < 0 ? null : chunk.getTile(x - 1, y);
                if ((top == null || !top.isSolid()) || (bot == null || !bot.isSolid())
                        || (right == null || !right.isSolid()) || (left == null || !left.isSolid())) {
                    if (t.isSolid()) {
                        createFixture(t, x, y);
                    }
                }
            }
        }
        return b;
    }
    
    @Override
    public void destroyBody(Body body, World world) {
        for (int i = 0; i < Chunk.CHUNK_TILE_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_TILE_SIZE; j++) {
                int x = chunk.getGlobalTileX() + i;
                int y = chunk.getGlobalTileY() + j;
                TileState state = chunk.getTileState(x, y);
                state.setFixture(null);
            }
        }
        this.body = null;
        BodyFactory.super.destroyBody(body, world);
    }
    
    private void createFixture(Tile tile, int gtx, int gty) { //This could be more memory efficient
        PolygonShape shape = new PolygonShape();
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.restitution = tile.getBouncyness();
        shape.setAsBox(METER_CONV.in(Tile.TILE_SIZE / 2), METER_CONV.in(Tile.TILE_SIZE / 2),
                new Vector2(METER_CONV.in((gtx - chunk.getGlobalTileX()) * Tile.TILE_SIZE),
                        METER_CONV.in((gty - chunk.getGlobalTileY()) * Tile.TILE_SIZE)),
                0);
        Fixture fix = body.createFixture(fd);
        chunk.getTileState(gtx, gty).setFixture(fix);
        shape.dispose();
        fix.setUserData(tile);//Enough for now
    }
    
    private final class ListenerClass implements ChunkChangeListener {
        @Override
        public void onTileStateChange(Chunk chunk, TileState state, Tile newTile, Tile oldTile, int gtx, int gty) {
            if (body == null) {
                //The chunk hasn't been added to the simulation yet
                return;
            }
            if (oldTile.isSolid() != newTile.isSolid()) {
                if (!newTile.isSolid() && body != null) { //oldstate was solid, newstate isn't
                    if (state.getFixture() != null) { //the fixture is null if oldstate was surrounded by solid tiles
                        destroyFixture(state);
                    }
                    int x = gtx;
                    int y = gty;
                    int topy = y + 1;
                    int boty = y - 1;
                    int rightx = x + 1;
                    int leftx = x - 1;
                    TileState top = chunk.inBounds(x, topy) ? chunk.getTileState(x, topy) : null;
                    TileState bot = chunk.inBounds(x, boty) ? chunk.getTileState(x, boty) : null;
                    TileState right = chunk.inBounds(rightx, y) ? chunk.getTileState(rightx, y) : null;
                    TileState left = chunk.inBounds(leftx, y) ? chunk.getTileState(leftx, y) : null;
                    if (top != null && top.getTile().isSolid() && top.getFixture() == null) {
                        createFixture(top.getTile(), x, topy);
                    }
                    if (bot != null && bot.getTile().isSolid() && bot.getFixture() == null) {
                        createFixture(bot.getTile(), x, boty);
                    }
                    if (right != null && right.getTile().isSolid() && right.getFixture() == null) {
                        createFixture(right.getTile(), rightx, y);
                    }
                    if (left != null && left.getTile().isSolid() && left.getFixture() == null) {
                        createFixture(left.getTile(), leftx, y);
                    }
                } else { //newstate is solid, oldstate wasn't
                    int x = gtx;
                    int y = gty;
                    int topy = y + 1;
                    int boty = y - 1;
                    int rightx = x + 1;
                    int leftx = x - 1;
                    TileState top = chunk.inBounds(x, topy) ? chunk.getTileState(x, topy) : null;
                    TileState bot = chunk.inBounds(x, boty) ? chunk.getTileState(x, boty) : null;
                    TileState right = chunk.inBounds(rightx, y) ? chunk.getTileState(rightx, y) : null;
                    TileState left = chunk.inBounds(leftx, y) ? chunk.getTileState(leftx, y) : null;
                    if ((top == null || !top.getTile().isSolid()) || (bot == null || !bot.getTile().isSolid())
                            || (right == null || !right.getTile().isSolid())
                            || (left == null || !left.getTile().isSolid())) {
                        createFixture(newTile, x, y);
                    }
                    //check if the neighbouring fixtures can be removed, this depends on the neighbours neighbours
                    checkDestroy(top, x, topy);
                    checkDestroy(bot, x, boty);
                    checkDestroy(right, rightx, y);
                    checkDestroy(left, leftx, y);
                }
            } else {
                //new and old are both solid or both not solid. The fixture is retained if any.
                //The restitution might have to change
                if (oldTile.isSolid() && state.getFixture() != null) {
                    //                    newstate.setFixture(state.getFixture());
                    //                    oldstate.setFixture(null);//Doesn't seem necessary but whatever
                    state.getFixture().setRestitution(newTile.getBouncyness());
                }
            }
        }
        
        private void checkDestroy(TileState state, int tx, int ty) {
            if (state != null && state.getTile().isSolid()) {
                int topty = ty + 1;
                int botty = ty - 1;
                int righttx = tx + 1;
                int lefttx = tx - 1;
                Tile bot = chunk.inBounds(tx, botty) ? chunk.getTile(tx, botty) : null;
                Tile top = chunk.inBounds(tx, topty) ? chunk.getTile(tx, topty) : null;
                Tile right = chunk.inBounds(righttx, ty) ? chunk.getTile(righttx, ty) : null;
                Tile left = chunk.inBounds(lefttx, ty) ? chunk.getTile(lefttx, ty) : null;
                if ((top != null && top.isSolid()) && (right != null && right.isSolid())
                        && (left != null && left.isSolid()) && (bot != null && bot.isSolid())) {
                    destroyFixture(state);
                }
            }
        }
        
        private void destroyFixture(TileState state) {
            body.destroyFixture(state.getFixture());
            state.setFixture(null);
        }
        
    }
    
    @Override
    public Vector2 bodyOffset() {
        return BODY_OFFSET;
    }
    
}
