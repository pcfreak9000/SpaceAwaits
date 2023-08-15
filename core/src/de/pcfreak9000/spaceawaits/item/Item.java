package de.pcfreak9000.spaceawaits.item;

import java.util.Objects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.comp.Composite;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.module.IModule;
import de.pcfreak9000.spaceawaits.module.ModuleHolder;
import de.pcfreak9000.spaceawaits.module.ModuleID;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

/**
 * represents an Item
 *
 * @author pcfreak9000
 *
 */
public class Item {
    
    public static final float WORLD_SIZE = 0.6f;
    
    private ITextureProvider textureProvider = TextureProvider.EMPTY;
    
    private int maxstacksize = ItemStack.MAX_STACKSIZE;
    
    private Color color = Color.WHITE;
    private String displayName = "";
    
    private Composite composite;
    
    private ModuleHolder modules = new ModuleHolder();
    
    public <T extends IModule> T getModule(ModuleID id) {
        return modules.getModule(id);
    }
    
    public boolean hasModule(ModuleID id) {
        return modules.hasModule(id);
    }
    
    public Item addModule(ModuleID addAsId, IModule module) {
        modules.addModule(addAsId, module);
        return this;
    }
    
    public Item setTexture(String name) {
        setTextureProvider(TextureProvider.get(name));
        return this;
    }
    
    public Item setTextureProvider(ITextureProvider prov) {
        Objects.requireNonNull(prov);
        this.textureProvider = prov;
        return this;
    }
    
    public int getMaxStackSize() {
        return this.maxstacksize;
    }
    
    public void setMaxStackSize(int i) {
        this.maxstacksize = i;
    }
    
    public Item setColor(Color color) {
        this.color = color;
        return this;
    }
    
    public Color getColor() {
        return color;
    }
    
    public Item setDisplayName(String name) {
        this.displayName = name;
        return this;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setComposite(Composite composite) {
        this.composite = composite;
    }
    
    public Composite getComposite() {
        return composite;
    }
    
    public ITextureProvider getIcon() {
        return textureProvider;
    }
    
    public boolean onItemUse(Player player, ItemStack stackUsed, World world, float x, float y, int tilex, int tiley,
            TileLayer layer) {
        return false;
    }
    
    public boolean onItemJustUse(Player player, ItemStack stackUsed, World world, float x, float y, int tilex,
            int tiley, TileLayer layer) {
        return false;
    }
    
    public boolean onItemBreakTile(Player player, ItemStack stackUsed, World world, float x, float y, TileSystem tiles,
            int tx, int ty, TileLayer layer) {
        return false;
    }
    
    public boolean onItemBreakAttackEntity(Player player, ItemStack stackUsed, World world, float x, float y,
            Entity entity) {
        return false;
    }
    
    //Forward break attack stuff only if target is inside max range
    //what about context, like what is being targeted, etc??? also for rendering purposes of highlighting possible actions maybe? 
    //-> item vs tile
    public float getMaxRangeBreakAttack(Player player, ItemStack stackUsed) {
        return 0;
    }
    
    public float getMaxRangeUse(Player player, ItemStack stackUsed) {
        return 0;
    }
    
    public float getReach(Player player, ItemStack stack) {
        return player.getReach();
    }
    
    //-> SpecialBreakAttackModule?
    public boolean isSpecialBreakAttack() {
        return false;
    }
    
    public boolean onItemSpecialBreakAttack(Player player, ItemStack stackUsed, World world, float x, float y, int tx,
            int ty, TileLayer layer) {
        return false;
    }
    
    @Override
    public String toString() {
        return "Item [maxstacksize=" + maxstacksize + ", displayName=" + displayName + ", registryId="
                + Registry.ITEM_REGISTRY.getId(this) + "]";
    }
    
}
