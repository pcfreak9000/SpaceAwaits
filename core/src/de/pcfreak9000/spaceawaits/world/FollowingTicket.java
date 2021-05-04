package de.pcfreak9000.spaceawaits.world;

import com.badlogic.gdx.math.Vector2;

import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public class FollowingTicket implements ITicket {
    
    private IntCoords[] coords;
    
    private Vector2 pos;
    
    private int radius;
    
    public FollowingTicket(Vector2 pos) {
        this.pos = pos;
        updateRange(2);
    }
    
    public void updateRange(int radius) {
        this.radius = radius;
        int dia = radius * 2 + 1;
        coords = new IntCoords[dia * dia];
        for (int i = 0; i < coords.length; i++) {
            coords[i] = new IntCoords(0, 0);
        }
    }
    
    @Override
    public void update(World world, float dt) {
        int gcx = Chunk.toGlobalChunkf(pos.x);
        int gcy = Chunk.toGlobalChunkf(pos.y);
        int count = 0;
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                coords[count].setCoords(gcx + i, gcy + j);
                count++;
            }
        }
    }
    
    @Override
    public boolean isValid() {
        return true;
    }
    
    @Override
    public IntCoords[] getLoadChunks() {
        return coords;
    }
    
}
