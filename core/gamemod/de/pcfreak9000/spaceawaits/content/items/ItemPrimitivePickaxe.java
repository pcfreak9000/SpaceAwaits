package de.pcfreak9000.spaceawaits.content.items;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.Breakable;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class ItemPrimitivePickaxe extends Item {
    
    public ItemPrimitivePickaxe() {
        this.setMaxStackSize(1);
        this.setDisplayName("Primitive Pickaxe");
        this.setTexture("shitty_pickaxe.png");
    }
    
    @Override
    public boolean onItemBreakTile(Player player, ItemStack stackUsed, World world, float x, float y, TileSystem tiles,
            int tx, int ty, TileLayer layer) {
        return tiles.breakTile(tx, ty, layer, tilebreaker);
    }
    
    private final IBreaker tilebreaker = new IBreaker() {
        
        @Override
        public float breakIt(World world, Breakable breakable, float f) {
            return 15 / breakable.getHardness();
        }
        
        @Override
        public boolean canBreak(World world, Breakable breakable) {
            return true;
        }
        
        @Override
        public void onBreak(World world, Breakable breakable, Array<ItemStack> drops, Random random) {
        }
        
    };
}
