package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Objects;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.item.ItemTile;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.physics.IContactListener;

public class Tile {
    
    public static enum TileLayer {
        Front, Back;
    }
    
    public static final float MAX_LIGHT_VALUE = 16;
    public static final Tile NOTHING = new Tile();//The last bastion against the void
    
    public static int toGlobalTile(float x) {
        return Mathf.floori(x);//well i hope this floor function works properly
    }
    
    static {
        NOTHING.setBouncyness(0);
        NOTHING.setCanBreak(false);
        NOTHING.setLightColor(null);
        NOTHING.setOpaque(false);
        NOTHING.setTexture(null);
        NOTHING.setSolid(false);
        NOTHING.setCanBeReplaced(true);
        NOTHING.color().set(0, 0, 0, 0);
        GameRegistry.TILE_REGISTRY.register("empty", NOTHING);
    }
    
    private ITextureProvider textureProvider;
    
    private boolean canBreak = true;
    private boolean opaque = true;
    private boolean solid = true;
    private boolean canBeReplaced = false;
    
    private final Color color = new Color(1, 1, 1, 1);
    
    private Color lightColor;
    private float lighttransmission = 0.8f;
    
    private float bouncyness = 0f;
    
    private float materialLevel = 0f;
    private float hardness = 1f;
    
    private Item itemTile;
    
    public void setTexture(String name) {
        setTextureProvider(TextureProvider.get(name));
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
    
    public float getMaterialLevel() {
        return materialLevel;
    }
    
    public void setMaterialLevel(float materialLevel) {
        this.materialLevel = materialLevel;
    }
    
    public float getHardness() {
        return hardness;
    }
    
    public void setHardness(float hardness) {
        this.hardness = hardness;
    }
    
    public boolean canBeReplaced() {
        return canBeReplaced;
    }
    
    public void setCanBeReplaced(boolean canBeReplaced) {
        this.canBeReplaced = canBeReplaced;
    }
    
    public final Item getRegisterItem() {
        if (this.itemTile == null) {
            this.itemTile = createItem();
            return this.itemTile;
        }
        return null;
    }
    
    protected Item createItem() {
        return new ItemTile(this);
    }
    
    public Item getItemTile() {
        return itemTile;
    }
    
    public void onTileBroken(int tx, int ty, TileLayer layer, Array<ItemStack> drops, World world, Random random) {
        drops.add(new ItemStack(getItemTile(), 1));
    }
    
    public void onTileRemoved(int tx, int ty, TileLayer layer, World world) {
        
    }
    
    public void onTileSet(int tx, int ty, TileLayer layer, World world) {
        
    }
    
    public void onTilePlaced(int tx, int ty, TileLayer layer, World world) {
        
    }
    
    public void updateTick(int tx, int ty, TileLayer layer, World world, int tick) {
        
    }
    
    public boolean hasTileEntity() {
        return false;
    }
    
    public TileEntity createTileEntity(World world, int gtx, int gty) {
        return null;
    }
    
    public boolean onTileUse(Player player, World world, ItemStack stackUsed, int gtx, int gty) {
        return false;
    }
    
    public IContactListener getContactListener() {
        return null;
    }
    
    public void onNeighbourChange(World world, int gtx, int gty, Tile newNeighbour, Tile oldNeighbour, int ngtx,
            int ngty) {
    }
    
    public boolean hasMetadata() {
        return false;
    }
    
    public IMetadata createMetadata() {
        return null;
    }
    
    @Override
    public String toString() {
        return "Tile [textureProvider=" + textureProvider + ", canBreak=" + canBreak + ", opaque=" + opaque + ", solid="
                + solid + ", color=" + color + ", lightColor=" + lightColor + ", lighttransmission=" + lighttransmission
                + ", bouncyness=" + bouncyness + "]";
    }
    
    public ITextureProvider getTextureProvider() {
        return textureProvider == null ? TextureProvider.EMPTY : textureProvider;
    }
}
