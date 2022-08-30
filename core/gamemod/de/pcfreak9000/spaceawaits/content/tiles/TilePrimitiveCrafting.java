package de.pcfreak9000.spaceawaits.content.tiles;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.content.ContainerCrafter;
import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
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
            int gty) {
        // TileEntityStorageDrawer te = (TileEntityStorageDrawer) tileSystem.getTileEntity(gtx, gty, TileLayer.Front);
        //  player.openContainer(new ContainerStorageDrawer(te));
        player.openContainer(new ContainerCrafter(3));
        return true;
    }
    
    @Override
    public void onBreak(World world, Array<ItemStack> drops, Random random, TileSystem tiles, int tx, int ty,
            TileLayer layer) {
        super.onBreak(world, drops, random, tiles, tx, ty, layer);
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