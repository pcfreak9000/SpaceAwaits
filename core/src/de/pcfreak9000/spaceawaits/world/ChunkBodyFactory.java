package de.pcfreak9000.spaceawaits.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.world.tile.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.ChunkChangeListener;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.TileState;

public class ChunkBodyFactory implements BodyFactory, ChunkChangeListener {
    
    private Chunk chunk;
    private Body body;
    
    public ChunkBodyFactory(Chunk chunk) {
        this.chunk = chunk;
        this.chunk.addListener(this);
    }
    
    @Override
    public Body createBody(World world, UnitConversion meterconv) {
        
    }
    
    @Override
    public void onTileStateChange(Chunk chunk, TileState newstate, TileState oldstate) {
        Array<Fixture> fixs = this.body.getFixtureList();
        fixs.get(0).getUserData()
    }
    
}
