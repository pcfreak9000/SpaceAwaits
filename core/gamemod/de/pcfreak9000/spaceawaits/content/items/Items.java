package de.pcfreak9000.spaceawaits.content.items;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;

public class Items {
    
    public static final Item MEDKIT_SIMPLE = new ItemMedkitSimple();
    public static final Item AXE_PRIMITIVE = new ItemPrimitiveAxe();
    public static final Item PICKAXE_PRIMITIVE = new ItemPrimitivePickaxe();
    public static final Item TWIG = new Item();
    public static final Item LOOSEROCK = new Item();
    public static final Item REPAIRGUN = new ItemRepairGun();
    
    public static void registerItems() {
        //@formatter:off
        GameRegistry.ITEM_REGISTRY.register("medkitSimple", MEDKIT_SIMPLE);
        GameRegistry.ITEM_REGISTRY.register("axePrimitive", AXE_PRIMITIVE);
        GameRegistry.ITEM_REGISTRY.register("pickaxePrimitive", PICKAXE_PRIMITIVE);
        GameRegistry.ITEM_REGISTRY.register("twig", TWIG.setTexture("stick.png").setDisplayName("Twig"));
        GameRegistry.ITEM_REGISTRY.register("looserock", LOOSEROCK.setTexture("looserock.png").setDisplayName("Loose Rock"));
        GameRegistry.ITEM_REGISTRY.register("repairgun", REPAIRGUN);
        //@formatter:on
    }
    
}
