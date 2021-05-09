import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.ITileBreaker;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.physics.IRaycastTileCallback;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class ItemGun extends Item {
    @Override
    public boolean onItemAttack(Player player, ItemStack stackUsed, World world, int tx, int ty, float x, float y) {
        world.raycastTiles(new IRaycastTileCallback() {
            
            @Override
            public boolean reportRayTile(Tile tile, int tx, int ty) {
                if (tile != Tile.EMPTY) {
                    world.breakTile(tx, ty, TileLayer.Front, tilebreaker);
                }
                return tile == Tile.EMPTY;
            }
        }, player.getX() + 1, player.getY() + 2, x, y, TileLayer.Front);
        return true;
    }
    
    private final ITileBreaker tilebreaker = new ITileBreaker() {
        
        @Override
        public void onBreak(int tx, int ty, TileLayer layer, Tile tile, World world) {
            ItemStack s = new ItemStack(tile.getItemTile(), 1);
            world.dropItemStack(s, tx + 0.5f - Item.WORLD_SIZE / 2f, ty + 0.5f - Item.WORLD_SIZE / 2f);
        }
        
        @Override
        public float getSpeed() {
            return 15;
        }
        
        @Override
        public float getMaterialLevel() {
            return Float.POSITIVE_INFINITY;
        }
    };
}
