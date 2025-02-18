package de.pcfreak9000.spaceawaits.content;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.content.entities.Entities;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.content.tiles.Tiles;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.crafting.BlastFurnaceRecipe;
import de.pcfreak9000.spaceawaits.crafting.FurnaceRecipe;
import de.pcfreak9000.spaceawaits.crafting.ShapedRecipe;
import de.pcfreak9000.spaceawaits.crafting.SimpleRecipe;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.mod.Mod;
import de.pcfreak9000.spaceawaits.registry.OreDict;
import de.pcfreak9000.spaceawaits.science.Science;

@Mod(id = "SpaceAwaits-Game", name = "Space Awaits Main Game", version = { 0, 0, 1 })
public class GameMod {
    
    @EventSubscription
    public void init(final CoreEvents.InitEvent init) {
        //Components.registerComponents();
        Items.registerItems();
        Tiles.registerTiles();
        Entities.registerEntities();
        Science.OBSERVATION_REGISTRY.register("testsss", new TestObservation());
        OreDict.addEntry("ingotIron", Items.INGOT_UNREFINED_IRON);
        OreDict.addEntry("ingotIron", Items.INGOT_IRON);
    }
    
    @EventSubscription
    public void postinit(CoreEvents.PostInitEvent ev) {
        SimpleRecipe.add(new ItemStack(Items.AXE_PRIMITIVE, 1), new ItemStack(Items.TWIG, 2),
                new ItemStack(Items.LOOSEROCK, 2));
        SimpleRecipe.add(new ItemStack(Items.SHOVEL_PRIMITIVE, 1), new ItemStack(Items.TWIG, 2),
                new ItemStack(Items.LOOSEROCK, 1));
        SimpleRecipe.add(new ItemStack(Items.PICKAXE_PRIMITIVE, 1), new ItemStack(Items.TWIG, 2),
                new ItemStack(Items.LOOSEROCK, 3));
        SimpleRecipe.add(new ItemStack(Tiles.WORKBENCH_PRIMITIVE, 1), new ItemStack(Tiles.WOOD, 1));
        
        ShapedRecipe.add(new ShapedRecipe(Tiles.FURNACE_PRIMITIVE, " X ", "X X", "XXX", 'X', Tiles.STONE));
        ShapedRecipe.add(new ShapedRecipe(new ItemStack(Items.STICK, 16), "X", "X", 'X', Tiles.WOOD));
        ShapedRecipe
                .add(new ShapedRecipe(Items.PICKAXE_SIMPLE, "XXX", " I ", " I ", 'X', "ingotIron", 'I', Items.STICK));
        ShapedRecipe.add(new ShapedRecipe(Tiles.FURNACE_BLAST, "- -", "-x-", "-x-", '-', "ingotIron", 'x',
                Tiles.FURNACE_PRIMITIVE));
        ShapedRecipe.add(new ShapedRecipe(new ItemStack(Tiles.TORCH, 4), "X", "I", 'X', Items.COAL, 'I', Items.STICK));
        ShapedRecipe.add(new ShapedRecipe(new ItemStack(Tiles.LADDER, 4), "XXX", "X X", "XXX", 'X', Items.STICK));
        ShapedRecipe.add(new ShapedRecipe(Tiles.STORAGE_DRAWER, "XXX", "X X", "XXX", 'X', "ingotIron"));
        FurnaceRecipe.add(new FurnaceRecipe(Items.INGOT_UNREFINED_IRON, Items.CLUMP_ORE_IRON));
        FurnaceRecipe.add(new FurnaceRecipe(Items.COKE, Items.COAL));
        BlastFurnaceRecipe.add(new BlastFurnaceRecipe(Items.INGOT_IRON, Items.CLUMP_ORE_IRON));
        //Maybe a quicker burntime? Maybe add normal furnace recipes if no matching blastfurnacerecipe is found?
        BlastFurnaceRecipe.add(new BlastFurnaceRecipe(Items.INGOT_IRON, Items.INGOT_UNREFINED_IRON));
        BlastFurnaceRecipe.add(new BlastFurnaceRecipe(Items.INGOT_COPPER, Items.CLUMP_ORE_COPPER));
        
        ShapedRecipe.add(new ShapedRecipe(Items.SIMPLE_PRESSURE_CHAMBER, " X ", "X X", " X ", 'X', Items.INGOT_IRON));
        ShapedRecipe.add(new ShapedRecipe(Items.SIMPLE_PISTON, "X", "X", 'X', Items.INGOT_IRON));
        ShapedRecipe.add(new ShapedRecipe(Items.SIMPLE_COIL, " C ", "CIC", " C ", 'C', Items.INGOT_COPPER, 'I',
                Items.INGOT_IRON));
        ShapedRecipe.add(new ShapedRecipe(new ItemStack(Items.SHEET_IRON, 12), "XX", "XX", 'X', Items.INGOT_IRON));
        ShapedRecipe.add(new ShapedRecipe(Items.SIMPLE_ELECTRIC_MOTOR, "XXX", "CCC", "XXX", 'X', Items.SHEET_IRON, 'C',
                Items.SIMPLE_COIL));
        ShapedRecipe.add(new ShapedRecipe(Items.SIMPLE_COMPRESSOR, "XMX", "XKX", "XPX", 'X', Items.SHEET_IRON, 'M',
                Items.SIMPLE_ELECTRIC_MOTOR, 'K', Items.SIMPLE_PISTON, 'P', Items.SIMPLE_PRESSURE_CHAMBER));
        ShapedRecipe.add(new ShapedRecipe(Items.JACKHAMMER, "XMX", " K ", " X ", 'X', Items.INGOT_IRON, 'M',
                Items.SIMPLE_ELECTRIC_MOTOR, 'K', Items.SIMPLE_PISTON));
        ShapedRecipe.add(
                new ShapedRecipe(Items.SIMPLE_BATTERY, "XCX", "XCX", "XXX", 'X', Items.INGOT_IRON, 'C', Items.COAL));
    }
}
