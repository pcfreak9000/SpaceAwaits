package de.pcfreak9000.spaceawaits.content.tiles;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.registry.GameRegistry;
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
    
    public static void registerTiles() {
        //@formatter:off
        GameRegistry.TILE_REGISTRY.register("storageDrawer", STORAGE_DRAWER);
        GameRegistry.TILE_REGISTRY.register("bricksOld", BRICKS_OLD.setTexture("oldbricks.png").setDisplayName("Old Bricks"));
        GameRegistry.TILE_REGISTRY.register("wood", WOOD.setTexture("oldbricks.png").setDisplayName("Wood").setColor(Color.BROWN));
        GameRegistry.TILE_REGISTRY.register("grass", GRASS.setTexture("grass.png").setDisplayName("Grass"));
        GameRegistry.TILE_REGISTRY.register("stone", STONE.setTexture("stone.png").setDisplayName("Stone").setLightTransmission(0.6f));
        GameRegistry.TILE_REGISTRY.register("looserocks", LOOSEROCKS);
        GameRegistry.TILE_REGISTRY.register("bedrock", BEDROCK.setTexture("stone_dark.png").setDisplayName("Bedrock or something").setCanBreak(false));
        GameRegistry.TILE_REGISTRY.register("dirt", DIRT.setTexture("dirt.png").setDisplayName("Dirt"));
        //@formatter:on
    }
}
