package de.pcfreak9000.spaceawaits.tileworld.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.pcfreak9000.spaceawaits.registry.GameRegistry;

public class Tile {
    
    public static final float MAX_LIGHT_VALUE = 16;
    
    public static final Tile EMPTY = new Tile();//TODO replace with usefulness
    
    public static final float TILE_SIZE = 16 * 1.5f;
    
    public static int toGlobalTile(float x) {
        return (int) Math.floor(x / (double) TILE_SIZE);//TODO Use other floor
    }
    
    static {
        EMPTY.setBouncyness(0);
        EMPTY.setCanBreak(false);
        EMPTY.setFilterColor(null);
        EMPTY.setLightColor(null);
        EMPTY.setOpaque(false);
        EMPTY.setTexture(null);
        EMPTY.setSolid(false);
        EMPTY.color().set(0, 0, 0, 0);
        //EMPTY.setLightLoss(0.0f);
        GameRegistry.TILE_REGISTRY.register("empty", EMPTY);
    }
    
    private String textureName = null;
    private TextureRegion texture = null;
    
    private boolean canBreak = true;
    private boolean opaque = false;
    private boolean solid = true;
    
    private final Color color = new Color(1,1,1,1);
    
    private Color lightColor;
    private float lightloss = 1;
    
    private Color filterColor;
    
    private float bouncyness = 0;
    
    public void setTexture(String name) {
        this.textureName = name;
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
        return this.lightColor != null && (this.lightColor.r >= 1 || this.lightColor.g >= 1 || this.lightColor.b >= 1);
    }
    
    public void setFilterColor(Color color) {
        this.filterColor = color;
    }
    
    public Color getFilterColor() {
        return this.filterColor;
    }
    
    public boolean hasLightFilter() {
        return this.filterColor != null;
    }
    
    public Color color() {
        return this.color;
    }
    
    public float getLightLoss() {
        return this.lightloss;
    }
    
    public void setLightLoss(float f) {
        this.lightloss = f;
    }
    
    public String getTextureName() {
        return textureName;
    }
    
    public boolean hasTileEntity() {
        return false;
    }
    
    public TileEntity createTileEntity(TileWorld world, TileState myState) {
        return null;
    }
    
    public void neighbourChanged(TileWorld world, TileState neighbour) {
        //?!?!?!?
    }
    
    @Override
    public String toString() {
        return String.format("Tile[texture=%s]", this.textureName);
    }
    
    //TODO this sucks:
    public void setTextureRegion(TextureRegion tex) {
        this.texture = tex;
    }
    public TextureRegion getTextureRegion() {
        return this.texture;
    }
}
