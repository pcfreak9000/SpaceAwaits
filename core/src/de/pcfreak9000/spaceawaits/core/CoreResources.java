package de.pcfreak9000.spaceawaits.core;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.WorldEntityFactory;

public class CoreResources {
    
    public static final void init() {
        Logger.getLogger(CoreResources.class).info("Creating core resource hooks");
    }
    
    public static final TextureProvider SPACE_BACKGROUND = TextureProvider.get("Space.png");
    public static final TextureProvider HUMAN = TextureProvider.get("mensch.png");
    
    public static final WorldEntityFactory PLAYER_FACTORY = GameRegistry.WORLD_ENTITY_REGISTRY
            .register("player", new PlayerEntityFactory()).get("player");//Meh...
}
