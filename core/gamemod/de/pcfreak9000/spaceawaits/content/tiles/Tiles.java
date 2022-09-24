package de.pcfreak9000.spaceawaits.content.tiles;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.content.Tools;
import de.pcfreak9000.spaceawaits.content.tiles.blastfurnace.TileBlastFurnace;
import de.pcfreak9000.spaceawaits.content.tiles.primitivefurnace.TilePrimitiveFurnace;
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
    public static final Tile WORKBENCH_PRIMITIVE = new TilePrimitiveCrafting();
    public static final Tile FURNACE_PRIMITIVE = new TilePrimitiveFurnace();
    public static final Tile STONE_DARK = new Tile();
    public static final Tile ORE_POOR_IRON = new TileOrePoorIron();
    public static final Tile ORE_IRON = new TileOreIron();
    public static final Tile FURNACE_BLAST = new TileBlastFurnace();
    public static final Tile ORE_COAL = new TileOreCoal();
    public static final Tile TORCH = new TileTorch();
    public static final Tile LADDER = new TileLadder();
    
    public static void registerTiles() {
        //@formatter:off
        GameRegistry.registerTile("storageDrawer", STORAGE_DRAWER);
        GameRegistry.registerTile("bricksOld", BRICKS_OLD.setTexture("oldbricks.png").setDisplayName("Old Bricks"));
        GameRegistry.registerTile("wood", WOOD.setTexture("wood.png").setDisplayName("Wood").setMaterialLevel(1f).setRequiredTool(Tools.AXE));
        GameRegistry.registerTile("grass", GRASS.setTexture("grass.png").setDisplayName("Grass").setRequiredTool(Tools.SHOVEL).setMaterialLevel(1f).setHardness(0.9f));
        GameRegistry.registerTile("stone", STONE.setTexture("stoneWhite.png").setColor(Color.GRAY).setDisplayName("Stone").setLightTransmission(0.7f).setMaterialLevel(1f).setRequiredTool(Tools.PICKAXE));
        GameRegistry.registerTile("stoneDark", STONE_DARK.setTexture("stoneWhite.png").setColor(Color.DARK_GRAY).setDisplayName("Dark Stone").setLightTransmission(0.5f).setMaterialLevel(2f).setRequiredTool(Tools.PICKAXE));
        GameRegistry.registerTile("looserocks", LOOSEROCKS);
        GameRegistry.registerTile("bedrock", BEDROCK.setTexture("stoneWhite.png").setColor(new Color(0.1f, 0.1f, 0.1f, 1)).setDisplayName("Bedrock or something").setCanBreak(false));
        GameRegistry.registerTile("dirt", DIRT.setTexture("dirt.png").setDisplayName("Dirt").setRequiredTool(Tools.SHOVEL).setMaterialLevel(1f).setHardness(0.9f));
        GameRegistry.registerTile("workbenchPrimitive", WORKBENCH_PRIMITIVE);
        GameRegistry.registerTile("furnacePrimitive", FURNACE_PRIMITIVE);
        GameRegistry.registerTile("orePoorIron", ORE_POOR_IRON);
        GameRegistry.registerTile("oreIron", ORE_IRON);
        GameRegistry.registerTile("furnaceBlast", FURNACE_BLAST);
        GameRegistry.registerTile("oreCoal", ORE_COAL);
        GameRegistry.registerTile("torch", TORCH);
        GameRegistry.registerTile("ladder", LADDER);
        //@formatter:on
    }
}
