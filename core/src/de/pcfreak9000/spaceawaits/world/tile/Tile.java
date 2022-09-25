package de.pcfreak9000.spaceawaits.world.tile;

import java.util.Objects;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.comp.Composite;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.world.Destructible;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.physics.IContactListener;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderMarkerComp;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderTileDefaultMarkerComponent;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class Tile extends Destructible {
    
    public static enum TileLayer {
        Front {
            @Override
            public TileLayer other() {
                return Back;
            }
        },
        Back {
            @Override
            public TileLayer other() {
                return Front;
            }
        };
        
        public TileLayer other() {
            throw new IllegalStateException();
        }
    }
    
    public static final float BACKGROUND_FACTOR = 0.55f;
    public static final float MAX_LIGHT_VALUE = 16;
    public static final Tile NOTHING = new Tile() {
        @Override
        public boolean canBeReplacedBy(Tile t) {
            return true;
        }
    };//The last bastion against the void
    
    public static int toGlobalTile(float x) {
        return Mathf.floori(x);//well i hope this floor function works properly
    }
    
    static {
        NOTHING.setBouncyness(0);
        NOTHING.setLightColor(null);
        NOTHING.setOpaque(false);
        NOTHING.setTexture(null);
        NOTHING.setSolid(false);
        NOTHING.setColor(Color.CLEAR);
        NOTHING.setCanBreak(false);
        Registry.TILE_REGISTRY.register("empty", NOTHING);
    }
    
    private ITextureProvider textureProvider = TextureProvider.EMPTY;
    
    private boolean opaque = true;
    private boolean solid = true;
    
    private Color color = Color.WHITE;
    
    private Color lightColor;
    private float lighttransmission = 0.8f;
    
    private float bouncyness = 0f;
    
    private String displayName;
    
    private Composite composite;
    
    public Tile setTexture(String name) {
        setTextureProvider(TextureProvider.get(name));
        return this;
    }
    
    public Tile setTextureProvider(ITextureProvider prov) {
        Objects.requireNonNull(prov);
        this.textureProvider = prov;
        return this;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Tile setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public void setBouncyness(float b) {
        this.bouncyness = b;
    }
    
    public float getBouncyness() {
        return this.bouncyness;
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
    
    //Maybe use a replacement mode e.g. if tile should be dropped or just be removed etc? 
    public boolean canBeReplacedBy(Tile t) {
        return false;
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
    
    public Tile setColor(Color color) {
        this.color = color;
        return this;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public float getLightTransmission() {
        return this.lighttransmission;
    }
    
    public Tile setLightTransmission(float f) {
        this.lighttransmission = f;
        return this;
    }
    
    public Composite getComposite() {
        return composite;
    }
    
    public void setComposite(Composite composite) {
        this.composite = composite;
    }
    
    public Item getItemTile() {
        return Registry.ITEM_REGISTRY.get(Registry.TILE_REGISTRY.getId(this));//If this becomes a problem possibly cache the result
    }
    
    public Item getItemDropped() {
        return getItemTile();
    }
    
    public int getDroppedQuantity() {
        return 1;
    }
    
    public void collectDrops(World world, Random random, int tx, int ty, TileLayer layer, Array<ItemStack> drops) {
        drops.add(new ItemStack(getItemDropped(), getDroppedQuantity()));
    }
    
    public void dropAsItemsInWorld(World world, Random random, int tx, int ty, TileLayer layer) {
        Array<ItemStack> drops = new Array<>();
        collectDrops(world, random, tx, ty, layer, drops);
        ItemStack.dropRandomInTile(drops, world, tx, ty);
    }
    
    public void onTileBreak(int tx, int ty, TileLayer layer, World world, TileSystem tiles, IBreaker breaker) {
    }
    
    public void onTileRemoved(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem) {
        
    }
    
    public void onTileSet(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem) {
        
    }
    
    public boolean canPlace(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem) {
        return true;
    }
    
    public void onTilePlaced(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem) {
        
    }
    
    public void updateTick(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem, long tick) {
        
    }
    
    public boolean hasTileEntity() {
        return false;
    }
    
    public ITileEntity createTileEntity(World world, int gtx, int gty, TileLayer layer) {
        return null;
    }
    
    public boolean onTileUse(Player player, World world, TileSystem tileSystem, ItemStack stackUsed, int gtx, int gty,
            TileLayer layer) {
        return false;
    }
    
    public boolean onTileJustUse(Player player, World world, TileSystem tileSystem, ItemStack stackUsed, int gtx,
            int gty, TileLayer layer) {
        return false;
    }
    
    public IContactListener getContactListener() {
        return null;
    }
    
    public void onNeighbourChange(World world, TileSystem tileSystem, int gtx, int gty, Tile newNeighbour,
            Tile oldNeighbour, int ngtx, int ngty, TileLayer layer) {
    }
    
    public boolean hasCustomHitbox() {
        return false;
    }
    
    public float[] getCustomHitbox() {
        return null;
    }
    
    public ITextureProvider getIcon(ItemStack stack) {
        return textureProvider;
    }
    
    public ITextureProvider getTextureProvider(int tx, int ty, TileLayer layer, ITileArea tiles) {
        return textureProvider;
    }
    
    public RenderMarkerComp getRendererMarkerComp() {
        return RenderTileDefaultMarkerComponent.INSTANCE;
    }
    
    /**** Inherited stuff, changed for easier chaining ****/
    
    @Override
    public Tile setCanBreak(boolean canBreak) {
        super.setCanBreak(canBreak);
        return this;
    }
    
    @Override
    public Tile setHardness(float hardness) {
        super.setHardness(hardness);
        return this;
    }
    
    @Override
    public Tile setMaterialLevel(float materialLevel) {
        super.setMaterialLevel(materialLevel);
        return this;
    }
    
    @Override
    public Tile setRequiredTool(String tool) {
        super.setRequiredTool(tool);
        return this;
    }
}
