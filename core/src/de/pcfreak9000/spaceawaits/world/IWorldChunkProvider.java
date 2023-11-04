package de.pcfreak9000.spaceawaits.world;

public interface IWorldChunkProvider extends IChunkProvider {
    
    void saveAll();
    
    void unloadAll();
    
    default void flush() {
    }
}
