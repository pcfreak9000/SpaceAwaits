package de.pcfreak9000.spaceawaits.core.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.DistanceFieldFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.InptMgr.ButtonKey;
import de.pcfreak9000.spaceawaits.core.ecs.EntityFactory;
import de.pcfreak9000.spaceawaits.item.ItemEntityFactory;
import de.pcfreak9000.spaceawaits.player.PlayerEntityFactory;
import de.pcfreak9000.spaceawaits.registry.Registry;

public class CoreRes {
    
    public static enum EnumInputIds {
        Left, Right, Down, Up, Esc, Use, BreakAttack, TestExplodeTiles, ToggleInventory, BackLayerMod,
        DebugScreenButton, DebugDrawPhysics, TestButton, Console, SendMsg, LastChatMsg, NextChatMsg, HideHud, INV_MOD,
        MovMod, CamZoom;
    }
    
    public static final BitmapFont FONT;//TODO font provider
    public static final SkinProvider SKIN = new SkinProvider();
    
    public static final ShaderProvider FOG_SHADER = new ShaderProvider("fog.vert");
    public static final ShaderProvider LIQUID_TRANSPARENT_SHADER = new ShaderProvider("liquid.vert");
    
    public static final Texture WHITE, MISSING_TEXTURE;
    
    public static final TextureProvider ITEM_HIGHLIGHT = TextureProvider.get("itemhighlight.png");
    
    public static final TextureProvider SPACE_BACKGROUND = TextureProvider.get("Space.png");
    public static final GeneratedTexture SPACE_BACKGROUND_2;
    
    public static final TextureProvider HUMAN = TextureProvider.get("Astronaut.png");
    
    public static final TextureProvider TILEMARKER_DEF = TextureProvider.get("backport.png");
    public static final TextureProvider ITEM_SLOT = TextureProvider.get("item_slot.png");
    
    public static final TextureProvider[] BREAK_OVERLAY = new TextureProvider[] { TextureProvider.get("brst1.png"),
            TextureProvider.get("brst2.png"), TextureProvider.get("brst3.png"), TextureProvider.get("brst4.png") };
    
    public static final EntityFactory PLAYER_FACTORY = Registry.WORLD_ENTITY_REGISTRY
            .register("player", new PlayerEntityFactory()).get("player");//Meh...
    public static final EntityFactory ITEM_FACTORY = Registry.WORLD_ENTITY_REGISTRY
            .register("item", new ItemEntityFactory()).get("item");
    
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
        InptMgr.register(EnumInputIds.MovMod, Keys.CONTROL_LEFT, false);
        InptMgr.register(EnumInputIds.CamZoom, Keys.CONTROL_LEFT, false);
    }
    
    static {
        Pixmap p = new Pixmap(1, 1, Format.RGB565);
        p.setColor(Color.WHITE);
        p.drawPixel(0, 0);
        WHITE = new Texture(p);
        p.dispose();
        Pixmap pmissing = new Pixmap(2, 2, Format.RGB888);
        pmissing.setColor(Color.BLACK);
        pmissing.drawPixel(0, 0);
        pmissing.drawPixel(1, 1);
        pmissing.setColor(Color.MAGENTA);
        pmissing.drawPixel(0, 1);
        pmissing.drawPixel(1, 0);
        MISSING_TEXTURE = new Texture(pmissing);
        MISSING_TEXTURE.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        pmissing.dispose();
        SPACE_BACKGROUND_2 = new GeneratedTexture(1920, 1080, new StarfieldTexGen(1500, 32 * 0.0288f * 1.3f / 1.5f));
        SPACE_BACKGROUND_2.create();
        //setup font
        Texture texture = new Texture(Gdx.files.internal("ui/dejavusans.png"), true); // true enables mipmaps
        texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
        FONT = new DistanceFieldFont(Gdx.files.internal("ui/dejavusans.fnt"), new TextureRegion(texture), false);
        ((DistanceFieldFont)FONT).setDistanceFieldSmoothing(10f);
        //FONT = new BitmapFont();
    }
    
    public static void dispose() {
        FONT.dispose();
        WHITE.dispose();
        MISSING_TEXTURE.dispose();
        SPACE_BACKGROUND_2.dispose();
    }
}
