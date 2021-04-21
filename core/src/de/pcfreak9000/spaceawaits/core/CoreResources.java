package de.pcfreak9000.spaceawaits.core;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.InptMgr.ButtonKey;
import de.pcfreak9000.spaceawaits.item.ItemEntityFactory;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.WorldEntityFactory;

public class CoreResources {
    
    public static enum EnumDefInputIds {
        Left, Right, Down, Up, Esc, Use, BreakAttack, TestExplodeTiles;
    }
    
    public static final void init() {
        Logger.getLogger(CoreResources.class).info("Creating core resource hooks");
        InptMgr.register(EnumDefInputIds.Left, Keys.A, false);
        InptMgr.register(EnumDefInputIds.Right, Keys.D, false);
        InptMgr.register(EnumDefInputIds.Up, new ButtonKey(Keys.W, false), new ButtonKey(Keys.SPACE, false));
        InptMgr.register(EnumDefInputIds.Down, new ButtonKey(Keys.S, false), new ButtonKey(Keys.SHIFT_LEFT, false));
        InptMgr.register(EnumDefInputIds.Esc, Keys.ESCAPE, false);
        InptMgr.register(EnumDefInputIds.Use, Buttons.RIGHT, true);
        InptMgr.register(EnumDefInputIds.BreakAttack, Buttons.LEFT, true);
        InptMgr.register(EnumDefInputIds.TestExplodeTiles, Buttons.MIDDLE, true);
        
    }
    
    public static final TextureProvider SPACE_BACKGROUND = TextureProvider.get("Space.png");
    public static final TextureProvider HUMAN = TextureProvider.get("mensch.png");
    
    public static final TextureProvider ITEM_SLOT = TextureProvider.get("item_slot.png");
    
    public static final WorldEntityFactory PLAYER_FACTORY = GameRegistry.WORLD_ENTITY_REGISTRY
            .register("player", new PlayerEntityFactory()).get("player");//Meh...
    public static final WorldEntityFactory ITEM_FACTORY = GameRegistry.WORLD_ENTITY_REGISTRY
            .register("item", new ItemEntityFactory()).get("item");
}
