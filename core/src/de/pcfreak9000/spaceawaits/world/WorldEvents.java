package de.pcfreak9000.spaceawaits.world;

import de.omnikryptec.event.Event;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.player.Player;

public class WorldEvents {
    
    public static class PlayerJoinedEvent extends Event {
        public final Player player;
        
        public PlayerJoinedEvent(Player player) {
            this.player = player;
        }
        
    }
    
    public static class PlayerLeftEvent extends Event {
        public final Player player;
        
        public PlayerLeftEvent(Player player) {
            this.player = player;
        }
    }
    
    public static class WorldMetaNBTEvent extends Event {
        
        public static enum Type {
            Writing, Reading;
        }
        
        public final NBTCompound worldMetaNbt;
        public final Type type;
        
        public WorldMetaNBTEvent(NBTCompound worldMetaNbt, Type type) {
            this.worldMetaNbt = worldMetaNbt;
            this.type = type;
        }
    }
    
    public static class WMNBTWritingEvent extends WorldMetaNBTEvent {
        
        public WMNBTWritingEvent(NBTCompound nbt) {
            super(nbt, Type.Writing);
            
        }
    }
    
    public static class WMNBTReadingEvent extends WorldMetaNBTEvent {
        
        public WMNBTReadingEvent(NBTCompound nbt) {
            super(nbt, Type.Reading);
            
        }
    }
}
