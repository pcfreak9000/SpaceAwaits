package de.pcfreak9000.spaceawaits.module;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.ITileEntity;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public interface IModuleTileEntity extends IModule {
    
    public static final ModuleID ID = ModuleID.getFor(IModuleTileEntity.class);
    
    ITileEntity createTileEntity(World world, int gtx, int gty, TileLayer layer);
}
