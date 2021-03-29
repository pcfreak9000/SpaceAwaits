package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Objects;

import com.badlogic.gdx.graphics.Color;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.TestWorldProvider;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;

public class Tile {
    
    public static final float MAX_LIGHT_VALUE = 16;
    
    public static final Tile EMPTY = new Tile();//The last bastion against the void
    
    public static final float TILE_SIZE = 16;
    
    public static int toGlobalTile(float x) {
        return Mathf.floori(x / TILE_SIZE);//well i hope this floor function works properly
    }
    
    static {
        EMPTY.setBouncyness(0);
        EMPTY.setCanBreak(false);
        EMPTY.setLightColor(null);
        EMPTY.setOpaque(false);
        EMPTY.setTexture(null);
        EMPTY.setSolid(false);
        EMPTY.color().set(0, 0, 0, 0);
        GameRegistry.TILE_REGISTRY.register("empty", EMPTY);
    }
    
    private ITextureProvider textureProvider;
    
    private boolean canBreak = true;
    private boolean opaque = true;
    private boolean solid = true;
    
    private final Color color = new Color(1, 1, 1, 1);
    
    private Color lightColor;
    private float lighttransmission = 0.8f;
    
    private float bouncyness = 0;
    
    public void setTexture(String name) {
        setTextureProvider(new TextureProvider(name));
    }
    
    public void setTextureProvider(ITextureProvider prov) {
        Objects.requireNonNull(prov);
        this.textureProvider = prov;
    }
    
    public void setBouncyness(float b) {
        this.bouncyness = b;
    }
    
    public float getBouncyness() {
        return this.bouncyness;
    }
    
    public void setCanBreak(boolean b) {
        this.canBreak = b;
    }
    
    public boolean canBreak() {
        return this.canBreak;
    }
    
    public void setOpaque(boolean b) {
        this.opaque = b;
    }
    
    public boolean isOpaque() {
        return this.opaque;
    }
    
    public void setSolid(boolean b) {
        this.solid = b;
    }
    
    public boolean isSolid() {
        return this.solid;
    }
    
    public void setLightColor(Color color) {
        this.lightColor = color;
    }
    
    public Color getLightColor() {
        return this.lightColor;
    }
    
    public boolean hasLight() {
        return this.lightColor != null && (this.lightColor.r > 0 || this.lightColor.g > 0 || this.lightColor.b > 0);
    }
    
    public Color color() {
        return this.color;
    }
    
    public float getLightTransmission() {
        return this.lighttransmission;
    }
    
    public void setLightTransmission(float f) {
        this.lighttransmission = f;
    }
    
    public boolean hasTileEntity() {
        return false;
    }
    
    public TileEntity createTileEntity(WorldAccessor world, TileState myState) {
        return null;
    }
    
    public void neighbourChanged(TestWorldProvider world, TileState neighbour) {
        //?!?!?!?
    }
    
    @Override
    public String toString() {
        return String.format("Tile[texture=%s]", Objects.toString(this.textureProvider));
    }
    
    public ITextureProvider getTextureProvider() {
        return textureProvider == null ? TextureProvider.EMPTY : textureProvider;
    }
}
