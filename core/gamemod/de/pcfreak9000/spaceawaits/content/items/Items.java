package de.pcfreak9000.spaceawaits.content.items;

import de.pcfreak9000.spaceawaits.content.modules.BurnModule;
import de.pcfreak9000.spaceawaits.content.modules.IBurnModule;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;

public class Items {
    
    public static final Item MEDKIT_SIMPLE = new ItemMedkitSimple();
    public static final Item AXE_PRIMITIVE = new ItemPrimitiveAxe();
    public static final Item PICKAXE_PRIMITIVE = new ItemPrimitivePickaxe();
    public static final Item SHOVEL_PRIMITIVE = new ItemPrimitiveShovel();
    public static final Item TWIG = new Item();
    public static final Item LOOSEROCK = new Item();
    //public static final Item REPAIRGUN = new ItemRepairGun();
    public static final Item CLUMP_ORE_IRON = new Item();
    public static final Item INGOT_UNREFINED_IRON = new Item();
    public static final Item INGOT_IRON = new Item();
    
    public static final Item CLUMP_ORE_COPPER = new Item();
    public static final Item INGOT_COPPER = new Item();
    
    public static final Item COAL = new Item();
    public static final Item COKE = new Item();
    
    public static final Item STICK = new Item();
    
    public static final Item PICKAXE_SIMPLE = new ItemSimplePickaxe();
    
    public static final Item JACKHAMMER = new ItemJackhammer();
    
    public static final Item CREATIVE_BREAKER = new ItemCreativeBreaker();
    
    public static final Item SIMPLE_PRESSURE_CHAMBER = new Item();
    public static final Item SIMPLE_COIL = new Item();
    public static final Item SHEET_IRON = new Item();
    public static final Item SIMPLE_ELECTRIC_MOTOR = new Item();
    public static final Item SIMPLE_COMPRESSOR = new Item();
    public static final Item SIMPLE_PISTON = new Item();
    
    public static final Item SIMPLE_BATTERY = new ItemSimpleBattery();
    
    public static void registerItems() {
        //@formatter:off
        GameRegistry.registerItem("pressureChamberSimple", SIMPLE_PRESSURE_CHAMBER.setTexture("pressureChamber.png").setDisplayName("Simple Pressure Chamber"));
        GameRegistry.registerItem("coilSimple", SIMPLE_COIL.setTexture("coil.png").setDisplayName("Simple Coil"));
        GameRegistry.registerItem("sheetIron", SHEET_IRON.setTexture("sheetIron.png").setDisplayName("Iron Sheet"));
        GameRegistry.registerItem("electricMotorSimple", SIMPLE_ELECTRIC_MOTOR.setTexture("electricmotor.png").setDisplayName("Simple Electric Motor"));
        GameRegistry.registerItem("compressorSimple", SIMPLE_COMPRESSOR.setTexture("compressor.png").setDisplayName("Simple Compressor"));
        GameRegistry.registerItem("pistonSimple", SIMPLE_PISTON.setTexture("piston.png").setDisplayName("Simple Piston"));
        GameRegistry.registerItem("crebreak", CREATIVE_BREAKER);
        GameRegistry.registerItem("medkitSimple", MEDKIT_SIMPLE);
        GameRegistry.registerItem("axePrimitive", AXE_PRIMITIVE);
        GameRegistry.registerItem("pickaxePrimitive", PICKAXE_PRIMITIVE);
        GameRegistry.registerItem("shovelPrimitive", SHOVEL_PRIMITIVE);
        GameRegistry.registerItem("twig", TWIG.setTexture("twig.png").setDisplayName("Twig").addModule(IBurnModule.ID, new BurnModule(60)));
        GameRegistry.registerItem("looserock", LOOSEROCK.setTexture("looserock.png").setDisplayName("Loose Rock"));
        GameRegistry.registerItem("clumpOreIron", CLUMP_ORE_IRON.setTexture("clumpOreIron.png").setDisplayName("Iron Ore Clump"));
        GameRegistry.registerItem("ingotUnrefinedIron", INGOT_UNREFINED_IRON.setTexture("ingotIronUnrefined.png").setDisplayName("Unrefined Iron Ingot"));
        GameRegistry.registerItem("ingotIron", INGOT_IRON.setTexture("ingotIron.png").setDisplayName("Iron Ingot"));
        GameRegistry.registerItem("stick", STICK.setTexture("stick.png").setDisplayName("Stick"));
        GameRegistry.registerItem("pickaxeSimple", PICKAXE_SIMPLE);
        GameRegistry.registerItem("coal", COAL.setTexture("coal.png").setDisplayName("Coal").addModule(IBurnModule.ID, new BurnModule(16 * 60)));
        GameRegistry.registerItem("coke", COKE.setTexture("coke.png").setDisplayName("Coke"));
        GameRegistry.registerItem("clumpOreCopper", CLUMP_ORE_COPPER.setTexture("clumpOreCopper.png").setDisplayName("Copper Ore Clump"));
        GameRegistry.registerItem("ingotCopper", INGOT_COPPER.setTexture("ingotCopper.png").setDisplayName("Copper Ingot"));
        GameRegistry.registerItem("jackhammer", JACKHAMMER);
        GameRegistry.registerItem("batterySimple", SIMPLE_BATTERY);
        //@formatter:on
    }
    
}
