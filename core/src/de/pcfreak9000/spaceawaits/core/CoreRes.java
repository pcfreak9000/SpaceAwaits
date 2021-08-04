package de.pcfreak9000.spaceawaits.core;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.InptMgr.ButtonKey;
import de.pcfreak9000.spaceawaits.item.ItemEntityFactory;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;

public class CoreRes {
    
    public static enum EnumDefInputIds {
        Left, Right, Down, Up, Esc, Use, BreakAttack, TestExplodeTiles, ToggleInventory, BackLayer, TestButton;
    }
    
    public static final void init() {
        Logger.getLogger(CoreRes.class).info("Creating core resource hooks");
        InptMgr.register(EnumDefInputIds.Left, Keys.A, false);
        InptMgr.register(EnumDefInputIds.Right, Keys.D, false);
        InptMgr.register(EnumDefInputIds.Up, new ButtonKey(Keys.W, false), new ButtonKey(Keys.SPACE, false));
        InptMgr.register(EnumDefInputIds.Down, new ButtonKey(Keys.S, false), new ButtonKey(Keys.SHIFT_LEFT, false));
        InptMgr.register(EnumDefInputIds.Esc, Keys.ESCAPE, false);
        InptMgr.register(EnumDefInputIds.Use, Buttons.RIGHT, true);
        InptMgr.register(EnumDefInputIds.BreakAttack, Buttons.LEFT, true);
        InptMgr.register(EnumDefInputIds.TestExplodeTiles, Buttons.MIDDLE, true);
        InptMgr.register(EnumDefInputIds.ToggleInventory, Keys.E, false);
        InptMgr.register(EnumDefInputIds.BackLayer, Keys.ALT_LEFT, false);
        InptMgr.register(EnumDefInputIds.TestButton, Keys.G, false);
    }
    
    static {
        Pixmap p = new Pixmap(1, 1, Format.RGB565);
        p.setColor(Color.WHITE);
        p.drawPixel(0, 0);
        WHITE = new Texture(p);
        p.dispose();
    }
    
    public static final ComponentMapper<TransformComponent> TRANSFORM_M = ComponentMapper
            .getFor(TransformComponent.class);
    
    public static final BitmapFont FONT = new BitmapFont();//TODO font provider
    public static final SkinProvider SKIN = new SkinProvider();
    
    public static final Texture WHITE;
    
    public static final TextureProvider SPACE_BACKGROUND = TextureProvider.get("Space.png");
    public static final TextureProvider HUMAN = TextureProvider.get("mensch.png");
    
    public static final TextureProvider ITEM_SLOT = TextureProvider.get("item_slot.png");
    
    public static final TextureProvider[] BREAK_OVERLAY = new TextureProvider[] { TextureProvider.get("brst1.png"),
            TextureProvider.get("brst2.png"), TextureProvider.get("brst3.png"), TextureProvider.get("brst4.png") };
    
    public static final WorldEntityFactory PLAYER_FACTORY = GameRegistry.WORLD_ENTITY_REGISTRY
            .register("player", new PlayerEntityFactory()).get("player");//Meh...
    public static final WorldEntityFactory ITEM_FACTORY = GameRegistry.WORLD_ENTITY_REGISTRY
            .register("item", new ItemEntityFactory()).get("item");
    
    public static void dispose() {
        FONT.dispose();
        WHITE.dispose();
    }
}
