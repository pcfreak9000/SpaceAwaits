package mod;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.Destructible;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastTileCallback;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;
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
    public float getMaxRangeBreakAttack(Player player, ItemStack stackUsed) {
        return Float.POSITIVE_INFINITY;
    }
    
    @Override
    public boolean onItemSpecialBreakAttack(Player player, ItemStack stackUsed, Engine world, float x, float y, int tx,
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
    
    private final IBreaker tilebreaker = new IBreaker() {
        
        @Override
        public float breakIt(Engine world, Destructible breakable, float f) {
            return 15 / breakable.getHardness();
        }
        
        @Override
        public boolean canBreak(Engine world, Destructible breakable) {
            return true;
        }
        
        @Override
        public void onBreak(Engine world, Destructible breakable, Array<ItemStack> drops, Random random) {
        }
        
    };
}
