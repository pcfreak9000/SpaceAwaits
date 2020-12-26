package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.ashley.core.Engine;

import de.omnikryptec.event.Event;
import de.pcfreak9000.spaceawaits.tileworld.tile.TileWorld;

public class WorldEvents {
    
    public static class InitWorldManagerEvent extends Event {
        public final Engine ecsMgr;
        
        public InitWorldManagerEvent(Engine mgr) {
            this.ecsMgr = mgr;
        }
    }
    
    public static class SetWorldEvent extends Event {
        public final World worldOld;
        public final World worldNew;
        public final WorldManager worldMgr;
        
        public SetWorldEvent(WorldManager wmgr, World worldOld, World worldNew) {
            this.worldNew = worldNew;
            this.worldOld = worldOld;
            this.worldMgr = wmgr;
        }
        
        public TileWorld getTileWorldNew() {
            return worldNew == null ? null : worldNew.getTileWorld();
        }
    }
    
}
