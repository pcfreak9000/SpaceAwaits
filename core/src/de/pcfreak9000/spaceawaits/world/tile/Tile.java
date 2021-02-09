package de.pcfreak9000.spaceawaits.world.tile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.TestWorldProvider;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;

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
    
    private TextureProvider texture = new TextureProvider();
    
    private boolean canBreak = true;
    private boolean opaque = true;
    private boolean solid = true;
    
    private final Color color = new Color(1,1,1,1);
    
    private Color lightColor;
    private float lighttransmission = 0.8f;
    
    private Color filterColor = new Color(0.99f, 0.99f, 0.99f, 0);
    
    private float bouncyness = 0;
    
    public void setTexture(String name) {
        this.texture.setTexture(name);
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
        //this.filterColor = color;
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
    
    public float getLightTransmission() {
        return this.lighttransmission;
    }
    
    public void setLightTransmission(float f) {
        this.lighttransmission = f;
    }
    
    public String getTextureName() {
        return texture.getName();
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
        return String.format("Tile[texture=%s]", this.getTextureName());
    }

    public TextureRegion getTextureRegion() {
        return this.texture.getRegion();
    }
}
