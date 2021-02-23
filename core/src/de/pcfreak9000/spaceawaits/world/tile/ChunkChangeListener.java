package de.pcfreak9000.spaceawaits.world.tile;

public interface ChunkChangeListener {
    //Hmmmmmmmmmmmmmmm....
    void onTileStateChange(Chunk chunk, TileState newstate, TileState oldstate);
}
