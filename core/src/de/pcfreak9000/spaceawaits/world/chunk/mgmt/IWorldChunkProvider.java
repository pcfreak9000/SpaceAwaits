package de.pcfreak9000.spaceawaits.world.chunk.mgmt;

public interface IWorldChunkProvider extends IChunkProvider {
    
    void unloadAll();
    
    default void flush() {
    }
}
