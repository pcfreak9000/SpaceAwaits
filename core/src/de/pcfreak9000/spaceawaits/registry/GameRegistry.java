package de.pcfreak9000.spaceawaits.registry;

import de.pcfreak9000.spaceawaits.crafting.CraftingManager;
import de.pcfreak9000.spaceawaits.crafting.ShapedRecipe;
import de.pcfreak9000.spaceawaits.crafting.SimpleRecipe;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class GameRegistry {
    
    public static void registerItem(String id, Item item) {
        Registry.ITEM_REGISTRY.register(id, item);
    }
    
    public static void registerTile(String id, Tile tile) {
        Registry.TILE_REGISTRY.register(id, tile);
    }
    
    public static void registerWorldEntity(String id, WorldEntityFactory wef) {
        Registry.WORLD_ENTITY_REGISTRY.register(id, wef);
    }
    
    public static void registerSimpleRecipe(SimpleRecipe sr) {
        CraftingManager.instance().addSimpleRecipe(sr);
    }
    
    public static void registerShapedRecipe(ShapedRecipe recipe) {
        CraftingManager.instance().addShapedRecipe(recipe);
    }
    
}
