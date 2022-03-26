package de.pcfreak9000.spaceawaits.world;

import de.omnikryptec.event.Event;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.ecs.SystemResolver;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;

public class WorldEvents {
    
    public static class SetupEntitySystemsEvent extends Event {
        public final World world;
        public final SystemResolver ecs;
        public final WorldPrimer worldPrimer;
        
        public SetupEntitySystemsEvent(World world, SystemResolver ecs, WorldPrimer worldPrimer) {
            this.world = world;
            this.ecs = ecs;
            this.worldPrimer = worldPrimer;
        }
    }
    
    public static class PlayerJoinedEvent extends Event {
        public final World world;
        public final Player player;
        
        public PlayerJoinedEvent(World world, Player player) {
            this.world = world;
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
    
}
