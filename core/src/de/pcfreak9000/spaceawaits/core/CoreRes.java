package de.pcfreak9000.spaceawaits.core;

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
import de.pcfreak9000.spaceawaits.player.PlayerEntityFactory;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;

public class CoreRes {
    
    public static enum EnumInputIds {
        Left, Right, Down, Up, Esc, Use, BreakAttack, TestExplodeTiles, ToggleInventory, BackLayerMod,
        DebugScreenButton, DebugDrawPhysics, TestButton, Console, SendMsg, LastChatMsg, NextChatMsg, HideHud, INV_MOD;
    }
    
    public static final void init() {
        Logger.getLogger(CoreRes.class).info("Creating core resource hooks");
        InptMgr.register(EnumInputIds.Left, Keys.A, false);
        InptMgr.register(EnumInputIds.Right, Keys.D, false);
        InptMgr.register(EnumInputIds.Up, new ButtonKey(Keys.W, false), new ButtonKey(Keys.SPACE, false));
        InptMgr.register(EnumInputIds.Down, new ButtonKey(Keys.S, false), new ButtonKey(Keys.SHIFT_LEFT, false));
        InptMgr.register(EnumInputIds.Esc, Keys.ESCAPE, false);
        InptMgr.register(EnumInputIds.Use, Buttons.RIGHT, true);
        InptMgr.register(EnumInputIds.BreakAttack, Buttons.LEFT, true);
        InptMgr.register(EnumInputIds.TestExplodeTiles, Buttons.MIDDLE, true);
        InptMgr.register(EnumInputIds.ToggleInventory, Keys.E, false);
        InptMgr.register(EnumInputIds.BackLayerMod, Keys.ALT_LEFT, false);
        InptMgr.register(EnumInputIds.TestButton, Keys.G, false);
        InptMgr.register(EnumInputIds.DebugScreenButton, Keys.F3, false);
        InptMgr.register(EnumInputIds.DebugDrawPhysics, Keys.F4, false);
        InptMgr.register(EnumInputIds.Console, Keys.ENTER, false);
        InptMgr.register(EnumInputIds.SendMsg, Keys.ENTER, false);
        InptMgr.register(EnumInputIds.LastChatMsg, Keys.UP, false);
        InptMgr.register(EnumInputIds.NextChatMsg, Keys.DOWN, false);
        InptMgr.register(EnumInputIds.HideHud, Keys.F1, false);
        InptMgr.register(EnumInputIds.INV_MOD, Keys.SHIFT_LEFT, false);
    }
    
    static {
        Pixmap p = new Pixmap(1, 1, Format.RGB565);
        p.setColor(Color.WHITE);
        p.drawPixel(0, 0);
        WHITE = new Texture(p);
        p.dispose();
    }
    
    public static final BitmapFont FONT = new BitmapFont();//TODO font provider
    public static final SkinProvider SKIN = new SkinProvider();
    
    public static final ShaderProvider FOG_SHADER = new ShaderProvider("fog.vert");
    public static final ShaderProvider LIQUID_TRANSPARENT_SHADER = new ShaderProvider("liquid.vert");
    
    public static final Texture WHITE;
    
    public static final TextureProvider SPACE_BACKGROUND = TextureProvider.get("Space.png");
    public static final TextureProvider HUMAN = TextureProvider.get("Astronaut.png");
    
    public static final TextureProvider TILEMARKER_DEF = TextureProvider.get("backport.png");
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
