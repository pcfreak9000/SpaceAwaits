package de.pcfreak9000.spaceawaits.content.tiles;

import de.pcfreak9000.spaceawaits.content.ContainerCrafter;
import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.IBreaker;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TilePrimitiveCrafting extends Tile {
    public TilePrimitiveCrafting() {
        this.setDisplayName("Primitive Workbench");
        this.setTexture("primitive_crafting.png");
        this.setMaterialLevel(1f);
        this.setRequiredTool(Tools.AXE);
    }
    
    @Override
    public boolean canPlace(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem) {
        return layer == TileLayer.Front;
    }
    
    @Override
    public boolean onTileJustUse(Player player, World world, TileSystem tileSystem, ItemStack stackUsed, int gtx,
            int gty, TileLayer layer) {
        player.openContainer(new ContainerCrafter(3));
        return true;
    }
    
    @Override
    public void onTileBreak(int tx, int ty, TileLayer layer, World world, TileSystem tiles, IBreaker breaker) {
        super.onTileBreak(tx, ty, layer, world, tiles, breaker);
        //Close gui if open, drop current contents?
    }
    
    @Override
    public boolean hasCustomHitbox() {
        return true;
    }
    
    @Override
    public float[] getCustomHitbox() {
        return new float[] { 0.05f, 0, /**/ 0.95f, 0, /**/0.95f, 0.7f, /**/0.05f, 0.7f };
    }
    
}
