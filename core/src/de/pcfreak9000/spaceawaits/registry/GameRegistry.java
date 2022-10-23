package de.pcfreak9000.spaceawaits.registry;

import java.lang.reflect.Constructor;

import com.google.common.collect.ObjectArrays;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemTile;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class GameRegistry {
    
//    public static int getBurnTime(Item item) {
//        if (item == null) {
//            return 0;
//        }
//        int max = 0;
//        for (IBurnHandler bh : burnhandlers) {
//            max = Math.max(max, bh.getBurnTime(item));
//        }
//        return max;
//    }
    
    public static void registerItem(String id, Item item) {
        Registry.ITEM_REGISTRY.register(id, item);
    }
    
    public static void registerTile(String id, Tile tile) {
        registerTile(id, tile, ItemTile.class);
    }
    
    public static void registerTile(String id, Tile tile, Class<? extends ItemTile> itemClazz) {
        registerTile(id, tile, itemClazz, new Object[0]);
    }
    
    public static void registerTile(String id, Tile tile, Class<? extends ItemTile> itemClazz, Object... args) {
        try {
            ItemTile itemTile = null;
            if (itemClazz != null) {
                Class<?>[] argClazzes = new Class<?>[args.length + 1];
                argClazzes[0] = Tile.class;
                for (int i = 1; i < argClazzes.length; i++) {
                    argClazzes[i] = args[i - 1].getClass();
                }
                Constructor<? extends ItemTile> itemCtor = itemClazz.getConstructor(argClazzes);
                itemTile = itemCtor.newInstance(ObjectArrays.concat(tile, args));
            }
            Registry.TILE_REGISTRY.register(id, tile);
            if (itemTile != null) {//Hmmm
                Registry.ITEM_REGISTRY.register(id, itemTile);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void registerWorldEntity(String id, WorldEntityFactory wef) {
        Registry.WORLD_ENTITY_REGISTRY.register(id, wef);
    }
    
}
