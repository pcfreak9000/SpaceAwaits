package de.pcfreak9000.spaceawaits.content.tiles;

import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class Tiles {
    
    public static final Tile STORAGE_DRAWER = new TileStorageDrawer();
    public static final Tile BRICKS_OLD = new Tile();
    public static final Tile GRASS = new Tile();
    public static final Tile WOOD = new Tile();
    public static final Tile STONE = new Tile();
    public static final Tile LOOSEROCKS = new TileLooseRocks();
    public static final Tile BEDROCK = new Tile();
    public static final Tile DIRT = new Tile();
    public static final Tile WORKBENCH_PRIMITIVE = new TilePrimitiveCrafting();
    
    public static void registerTiles() {
        //@formatter:off
        Registry.TILE_REGISTRY.register("storageDrawer", STORAGE_DRAWER);
        Registry.TILE_REGISTRY.register("bricksOld", BRICKS_OLD.setTexture("oldbricks.png").setDisplayName("Old Bricks"));
        Registry.TILE_REGISTRY.register("wood", WOOD.setTexture("wood.png").setDisplayName("Wood").setMaterialLevel(1f).setRequiredTool(Tools.AXE));
        Registry.TILE_REGISTRY.register("grass", GRASS.setTexture("grass.png").setDisplayName("Grass"));
        Registry.TILE_REGISTRY.register("stone", STONE.setTexture("stone.png").setDisplayName("Stone").setLightTransmission(0.6f).setMaterialLevel(1f).setRequiredTool(Tools.PICKAXE));
        Registry.TILE_REGISTRY.register("looserocks", LOOSEROCKS);
        Registry.TILE_REGISTRY.register("bedrock", BEDROCK.setTexture("stone_dark.png").setDisplayName("Bedrock or something").setCanBreak(false));
        Registry.TILE_REGISTRY.register("dirt", DIRT.setTexture("dirt.png").setDisplayName("Dirt"));
        Registry.TILE_REGISTRY.register("workbenchPrimitive", WORKBENCH_PRIMITIVE);
        //@formatter:on
    }
}
