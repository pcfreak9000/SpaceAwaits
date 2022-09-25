package de.pcfreak9000.spaceawaits.content.tiles;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

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
            int gty, TileLayer layer) {
        TileEntityStorageDrawer te = (TileEntityStorageDrawer) tileSystem.getTileEntity(gtx, gty, layer);
        player.openContainer(new ContainerStorageDrawer(te));
        return true;
    }
    
    @Override
    public void collectDrops(World world, Random random, int tx, int ty, TileLayer layer, Array<ItemStack> drops) {
        super.collectDrops(world, random, tx, ty, layer, drops);
        TileEntityStorageDrawer te = (TileEntityStorageDrawer) world.getSystem(TileSystem.class).getTileEntity(tx, ty,
                layer);
        for (int i = 0; i < te.slots(); i++) {
            drops.add(te.removeStack(i));
        }
    }
}
