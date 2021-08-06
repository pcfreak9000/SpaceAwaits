package de.pcfreak9000.spaceawaits.world;

import de.omnikryptec.event.Event;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.world.ecs.SystemResolver;

public class WorldEvents {
    @Deprecated //?
    public static class SetWorldEvent extends Event {
        
    }
    
    public static class SetupEntitySystemsEvent extends Event {
        public final World world;
        public final SystemResolver ecs;
        
        public SetupEntitySystemsEvent(World world, SystemResolver ecs) {
            this.world = world;
            this.ecs = ecs;
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
    
}
