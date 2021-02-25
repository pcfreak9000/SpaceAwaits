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
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystemBox2D;
import de.pcfreak9000.spaceawaits.world.physics.UnitConversion;

public class ChunkPhysics implements BodyFactory {
    
    private static final Vector2 BODY_OFFSET = new Vector2(0.5f * Tile.TILE_SIZE, 0.5f * Tile.TILE_SIZE);
    private final Chunk chunk;
    private Body body;
    
    public ChunkPhysics(Chunk chunk) {
        this.chunk = chunk;
        this.chunk.addListener(new ListenerClass());
    }
    
    @Override
    public Body createBody(World world, UnitConversion meterconv) {
        BodyDef bd = new BodyDef();
        bd.position.set(meterconv.in(chunk.getGlobalTileX() * Tile.TILE_SIZE + 0.5f * Tile.TILE_SIZE),
                meterconv.in(chunk.getGlobalTileY() * Tile.TILE_SIZE + 0.5f * Tile.TILE_SIZE));
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
                        createFixture(x, y);
                    }
                }
            }
        }
        return b;
    }
    
    @Override
    public void destroyBody(Body body, World world, UnitConversion meterconv) {
        for (int i = 0; i < Chunk.CHUNK_TILE_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_TILE_SIZE; j++) {
                int x = chunk.getGlobalTileX() + i;
                int y = chunk.getGlobalTileY() + j;
                TileState state = chunk.getTileState(x, y);
                state.setFixture(null);
            }
        }
        this.body = null;
        BodyFactory.super.destroyBody(body, world, meterconv);
    }
    
    private void createFixture(int gtx, int gty) {
        PolygonShape shape = new PolygonShape();
        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        shape.setAsBox(PhysicsSystemBox2D.METER_CONV.in(Tile.TILE_SIZE / 2),
                PhysicsSystemBox2D.METER_CONV.in(Tile.TILE_SIZE / 2),
                new Vector2(PhysicsSystemBox2D.METER_CONV.in((gtx - chunk.getGlobalTileX()) * Tile.TILE_SIZE),
                        PhysicsSystemBox2D.METER_CONV.in((gty - chunk.getGlobalTileY()) * Tile.TILE_SIZE)),
                0);
        Fixture fix = body.createFixture(fd);
        chunk.getTileState(gtx, gty).setFixture(fix);
        shape.dispose();
        //fix.setUserData(t); //TODO tile fixture user data
    }
    
    private final class ListenerClass implements ChunkChangeListener {
        @Override
        public void onTileStateChange(Chunk chunk, TileState newstate, TileState oldstate) {
            if (body == null) {
                //The chunk hasn't been added to the simulation yet
                return;
            }
            if (oldstate.getTile().isSolid() != newstate.getTile().isSolid()) {
                if (!newstate.getTile().isSolid() && body != null) { // oldstate was solid, newstate isn't
                    if (oldstate.getFixture() != null) {
                        body.destroyFixture(oldstate.getFixture());
                        oldstate.setFixture(null);
                    }
                    int x = newstate.getGlobalTileX();
                    int y = newstate.getGlobalTileY();
                    int topy = y + 1;
                    int boty = y - 1;
                    int rightx = x + 1;
                    int leftx = x - 1;
                    TileState top = chunk.inBounds(x, topy) ? chunk.getTileState(x, topy) : null;
                    TileState bot = chunk.inBounds(x, boty) ? chunk.getTileState(x, boty) : null;
                    TileState right = chunk.inBounds(rightx, y) ? chunk.getTileState(rightx, y) : null;
                    TileState left = chunk.inBounds(leftx, y) ? chunk.getTileState(leftx, y) : null;
                    if (top != null && top.getTile().isSolid() && top.getFixture() == null) {
                        createFixture(x, topy);
                    }
                    if (bot != null && bot.getTile().isSolid() && bot.getFixture() == null) {
                        createFixture(x, boty);
                    }
                    if (right != null && right.getTile().isSolid() && right.getFixture() == null) {
                        createFixture(rightx, y);
                    }
                    if (left != null && left.getTile().isSolid() && left.getFixture() == null) {
                        createFixture(leftx, y);
                    }
                } else { //newstate is solid, oldstate wasn't
                    int x = newstate.getGlobalTileX();
                    int y = newstate.getGlobalTileY();
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
                        createFixture(x, y);
                    }
                    //TODO check if the neighbouring fixtures can be removed
                    if (top != null && top.getTile().isSolid()) {
                        
                    }
                }
            }
        }
        
    }
    
    @Override
    public Vector2 bodyOffset() {
        return BODY_OFFSET;
    }
    
}
