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
    public static final Item CLUMP_ORE_IRON = new Item();
    public static final Item INGOT_IRON = new Item();
    public static final Item STICK = new Item();
    
    public static final Item CREATIVE_BREAKER = new ItemCreativeBreaker();
    
    public static void registerItems() {
        //@formatter:off
        GameRegistry.registerItem("crebreak", CREATIVE_BREAKER);
        GameRegistry.registerItem("medkitSimple", MEDKIT_SIMPLE);
        GameRegistry.registerItem("axePrimitive", AXE_PRIMITIVE);
        GameRegistry.registerItem("pickaxePrimitive", PICKAXE_PRIMITIVE);
        GameRegistry.registerItem("twig", TWIG.setTexture("twig.png").setDisplayName("Twig"));
        GameRegistry.registerItem("looserock", LOOSEROCK.setTexture("looserock.png").setDisplayName("Loose Rock"));
        GameRegistry.registerItem("repairgun", REPAIRGUN);
        GameRegistry.registerItem("clumpOreIron", CLUMP_ORE_IRON.setTexture("oreIronClump.png").setDisplayName("Iron Ore Clump"));
        GameRegistry.registerItem("ingotIron", INGOT_IRON.setTexture("ironIngot.png").setDisplayName("Iron Ingot"));
        GameRegistry.registerItem("stick", STICK.setTexture("stick.png").setDisplayName("Stick"));
        //@formatter:on
    }
    
}
