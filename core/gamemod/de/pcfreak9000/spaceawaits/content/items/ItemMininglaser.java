package de.pcfreak9000.spaceawaits.content.items;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.Breakable;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastTileCallback;
import de.pcfreak9000.spaceawaits.world.tile.ITileBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class ItemMininglaser extends Item {
    
    public ItemMininglaser() {
        this.setMaxStackSize(1);
        this.setDisplayName("Mining Laser");
        this.setTexture("gun_0.png");
    }
    
    @Override
    public boolean isSpecialBreakAttack() {
        return true;
    }
    
    @Override
    public boolean onItemSpecialBreakAttack(Player player, ItemStack stackUsed, World world, float x, float y, int tx,
            int ty, TileLayer layer) {
        TransformComponent tc = Components.TRANSFORM.get(player.getPlayerEntity());
        world.getSystem(TileSystem.class).raycastTiles(tc.position.x + 1, tc.position.y + 2, x, y, TileLayer.Front,
                new IRaycastTileCallback() {
                    
                    @Override
                    public boolean reportRayTile(Tile tile, int tx, int ty) {
                        if (tile != Tile.NOTHING && tile.isSolid()) {
                            world.getSystem(TileSystem.class).breakTile(tx, ty, TileLayer.Front, tilebreaker);
                        }
                        return tile == Tile.NOTHING || !tile.isSolid();
                    }
                });
        return true;
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
