package de.pcfreak9000.spaceawaits.content.tiles;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;
import mod.ContainerStorageDrawer;

public class TileStorageDrawer extends Tile {
    public TileStorageDrawer() {
        this.setDisplayName("Storage Drawer");
        this.setTexture("storagedrawer.png");
    }
    
    @Override
    public boolean hasTileEntity() {
        return true;
    }
    
    @Override
    public ITileEntity createTileEntity(World world, int gtx, int gty, TileLayer layer) {
        return new TileEntityStorageDrawer();
    }
    
    @Override
    public boolean canPlace(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem) {
        return layer == TileLayer.Front;
    }
    
    @Override
    public boolean onTileJustUse(Player player, World world, TileSystem tileSystem, ItemStack stackUsed, int gtx,
            int gty) {
        TileEntityStorageDrawer te = (TileEntityStorageDrawer) tileSystem.getTileEntity(gtx, gty, TileLayer.Front);
        player.openContainer(new ContainerStorageDrawer(te));
        return true;
    }
    
    @Override
    public void onBreak(World world, Array<ItemStack> drops, Random random, TileSystem tiles, int tx, int ty,
            TileLayer layer) {
        super.onBreak(world, drops, random, tiles, tx, ty, layer);
        TileEntityStorageDrawer te = (TileEntityStorageDrawer) tiles.getTileEntity(tx, ty, TileLayer.Front);
        for (int i = 0; i < te.slots(); i++) {
            drops.add(te.getStack(i));
        }
    }
}
