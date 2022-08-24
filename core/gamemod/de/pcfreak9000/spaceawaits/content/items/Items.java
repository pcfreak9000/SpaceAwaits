package de.pcfreak9000.spaceawaits.content.items;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;

public class Items {
    
    public static final Item MEDKIT_SIMPLE = new ItemMedkitSimple();
    public static final Item AXE_PRIMITIVE = new ItemPrimitiveAxe();
    public static final Item TWIG = new Item();
    public static final Item REPAIRGUN = new ItemRepairGun();
    public static final Item MININGLASER = new ItemMininglaser();

    public static void registerItems() {
        GameRegistry.ITEM_REGISTRY.register("medkitSimple", MEDKIT_SIMPLE);
        GameRegistry.ITEM_REGISTRY.register("axePrimitive", AXE_PRIMITIVE);
        GameRegistry.ITEM_REGISTRY.register("twig", TWIG.setTexture("stick.png").setDisplayName("Twig"));
        GameRegistry.ITEM_REGISTRY.register("repairgun", REPAIRGUN);
        GameRegistry.ITEM_REGISTRY.register("mininglaser", MININGLASER);

    }
    
}
