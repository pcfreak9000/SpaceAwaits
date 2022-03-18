package de.pcfreak9000.spaceawaits.world;

import de.omnikryptec.event.Event;
import de.pcfreak9000.spaceawaits.core.Player;
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
    
}
