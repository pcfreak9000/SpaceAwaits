package de.pcfreak9000.spaceawaits.content.items;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.Breakable;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.ITileBreaker;
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
        return tiles.breakTile(tx, ty, layer, tilebreaker) != -1f;
    }
    
    private final ITileBreaker tilebreaker = new ITileBreaker() {
        
        @Override
        public float breakIt(World world, Breakable breakable, int tx, int ty, TileLayer layer, float f) {
            return 15 / breakable.getHardness();
        }
        
        @Override
        public boolean canBreak(World world, Breakable breakable, int tx, int ty, TileLayer layer) {
            return true;
        }
        
        @Override
        public void onBreak(World world, Breakable breakable, int tx, int ty, TileLayer layer, Array<ItemStack> drops,
                Random random) {
        }
        
    };
}
