package de.pcfreak9000.spaceawaits.content.tiles.blastfurnace;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TileBlastFurnace extends Tile {
    public TileBlastFurnace() {
        this.setDisplayName("Blast Furnace");
        this.setTexture("blastfurnace.png");
        this.setMaterialLevel(1f);
        this.setRequiredTool(Tools.PICKAXE);
    }
    
    @Override
    public boolean canPlace(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem) {
        return layer == TileLayer.Front;
    }
    
    @Override
    public boolean onTileJustUse(Player player, World world, TileSystem tileSystem, ItemStack stackUsed, int gtx,
            int gty, TileLayer layer) {
        player.openContainer(
                new ContainerBlastFurnace((TileEntityBlastFurnace) tileSystem.getTileEntity(gtx, gty, layer)));
        return true;
    }
    
    @Override
    public boolean hasTileEntity() {
        return true;
    }
    
    @Override
    public ITileEntity createTileEntity(World world, int gtx, int gty, TileLayer layer) {
        return new TileEntityBlastFurnace();
    }
    
    @Override
    public void onBreak(World world, Array<ItemStack> drops, Random random, TileSystem tiles, int tx, int ty,
            TileLayer layer) {
        super.onBreak(world, drops, random, tiles, tx, ty, layer);
        TileEntityBlastFurnace te = (TileEntityBlastFurnace) tiles.getTileEntity(tx, ty, layer);
        for (int i = 0; i < te.slots(); i++) {
            drops.add(te.removeStack(i));
        }
    }
}
