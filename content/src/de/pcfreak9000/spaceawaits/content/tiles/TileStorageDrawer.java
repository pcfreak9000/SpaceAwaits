package de.pcfreak9000.spaceawaits.content.tiles;

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.module.IModuleTileEntity;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TileStorageDrawer extends Tile implements IModuleTileEntity {
    public TileStorageDrawer() {
        this.setDisplayName("Storage Drawer");
        this.setTexture("storagedrawer.png");
        addModule(ID, this);
    }
    
    @Override
    public ITileEntity createTileEntity(int gtx, int gty, TileLayer layer) {
        return new TileEntityStorageDrawer();
    }
    
    @Override
    public boolean canPlace(int tx, int ty, TileLayer layer, Engine world, TileSystem tileSystem) {
        return layer == TileLayer.Front;
    }
    
    @Override
    public boolean onTileJustUse(Player player, Engine world, TileSystem tileSystem, ItemStack stackUsed, int gtx,
            int gty, TileLayer layer) {
        TileEntityStorageDrawer te = (TileEntityStorageDrawer) tileSystem.getTileEntity(gtx, gty, layer);
        player.openContainer(new ContainerStorageDrawer(te));
        return true;
    }
    
    @Override
    public void collectDrops(Engine world, Random random, int tx, int ty, TileLayer layer, Array<ItemStack> drops) {
        super.collectDrops(world, random, tx, ty, layer, drops);
        TileEntityStorageDrawer te = (TileEntityStorageDrawer) world.getSystem(TileSystem.class).getTileEntity(tx, ty,
                layer);
        for (int i = 0; i < te.slots(); i++) {
            drops.add(te.removeStack(i));
        }
    }
}
