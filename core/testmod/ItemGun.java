import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.TileSystem;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastTileCallback;
import de.pcfreak9000.spaceawaits.world.tile.ITileBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class ItemGun extends Item {
    
    public ItemGun() {
        this.setMaxStackSize(1);
    }
    
    @Override
    public boolean onItemAttack(Player player, ItemStack stackUsed, World world, int tx, int ty, float x, float y) {
        TransformComponent tc = CoreRes.TRANSFORM_M.get(player.getPlayerEntity());
        world.getECS().getSystem(TileSystem.class).raycastTiles(new IRaycastTileCallback() {
            
            @Override
            public boolean reportRayTile(Tile tile, int tx, int ty) {
                if (tile != Tile.NOTHING) {
                    world.getECS().getSystem(TileSystem.class).breakTile(tx, ty, TileLayer.Front, tilebreaker);
                }
                return tile == Tile.NOTHING;
            }
        }, tc.position.x + 1, tc.position.y + 2, x, y, TileLayer.Front);
        return true;
    }
    
    private final ITileBreaker tilebreaker = new ITileBreaker() {
        
        @Override
        public float getSpeed() {
            return 15;
        }
        
        @Override
        public float getMaterialLevel() {
            return Float.POSITIVE_INFINITY;
        }
        
        @Override
        public void onTileBreak(int tx, int ty, TileLayer layer, Tile tile, World world, Array<ItemStack> drops,
                Random random) {
        }
        
        @Override
        public boolean canBreak(int tx, int ty, TileLayer layer, Tile tile, World world) {
            return true;
        }
    };
}
