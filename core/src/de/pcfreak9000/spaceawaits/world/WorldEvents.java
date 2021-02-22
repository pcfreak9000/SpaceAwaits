package de.pcfreak9000.spaceawaits.world;

import com.badlogic.ashley.core.Engine;

import de.omnikryptec.event.Event;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;

public class WorldEvents {
    
    public static class InitWorldManagerEvent extends Event {
        public final Engine ecsMgr;
        
        public InitWorldManagerEvent(Engine mgr) {
            this.ecsMgr = mgr;
        }
    }
    
    public static class SetWorldEvent extends Event {
        public final WorldProvider worldOld;
        public final WorldProvider worldNew;
        public final WorldManager worldMgr;
        
        public SetWorldEvent(WorldManager wmgr, WorldProvider worldOld, WorldProvider worldNew) {
            this.worldNew = worldNew;
            this.worldOld = worldOld;
            this.worldMgr = wmgr;
        }
        
    }
    
    public static class ChunkLoadedEvent extends Event {
        public final Chunk chunk;
        
        public ChunkLoadedEvent(Chunk c) {
            this.chunk = c;
        }
    }
    
    public static class ChunkUnloadedEvent extends Event {
        public final Chunk chunk;
        
        public ChunkUnloadedEvent(Chunk c) {
            this.chunk = c;
        }
    }
}
