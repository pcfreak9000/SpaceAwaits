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
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.registry.OreDict;

@Mod(id = "SpaceAwaits-Game", name = "Space Awaits Main Game", version = { 0, 0, 1 })
public class GameMod {
    @EventSubscription
    public void init(final CoreEvents.InitEvent init) {
        //Components.registerComponents();
        Items.registerItems();
        Tiles.registerTiles();
        Entities.registerEntities();
        GameRegistry.registerBurnHandler(new BurnHandler());
        
        OreDict.addEntry("ingotIron", Items.INGOT_IRON);
        OreDict.addEntry("ingotIron", Items.INGOT_REFINED_IRON);
    }
    
    @EventSubscription
    public void postinit(CoreEvents.PostInitEvent ev) {
        SimpleRecipe.add(new ItemStack(Items.AXE_PRIMITIVE, 1), new ItemStack(Items.TWIG, 2),
                new ItemStack(Items.LOOSEROCK, 2));
        SimpleRecipe.add(new ItemStack(Items.PICKAXE_PRIMITIVE, 1), new ItemStack(Items.TWIG, 2),
                new ItemStack(Items.LOOSEROCK, 3));
        SimpleRecipe.add(new ItemStack(Tiles.WORKBENCH_PRIMITIVE, 1), new ItemStack(Tiles.WOOD, 1));
        
        ShapedRecipe.add(new ShapedRecipe(Tiles.FURNACE_PRIMITIVE, " X ", "X X", "XXX", 'X', Tiles.STONE));
        ShapedRecipe.add(new ShapedRecipe(new ItemStack(Items.STICK, 4), "X", "X", 'X', Tiles.WOOD));
        ShapedRecipe
                .add(new ShapedRecipe(Items.PICKAXE_SIMPLE, "XXX", " I ", " I ", 'X', "ingotIron", 'I', Items.STICK));
        ShapedRecipe.add(new ShapedRecipe(Tiles.FURNACE_BLAST, "- -", "-x-", "-x-", '-', "ingotIron", 'x',
                Tiles.FURNACE_PRIMITIVE));
        ShapedRecipe.add(new ShapedRecipe(new ItemStack(Tiles.TORCH, 4), "X", "I", 'X', Items.COAL, 'I', Items.STICK));
        FurnaceRecipe.add(new FurnaceRecipe(Items.INGOT_IRON, Items.CLUMP_ORE_IRON));
        FurnaceRecipe.add(new FurnaceRecipe(Items.COKE, Items.COAL));
        BlastFurnaceRecipe.add(new BlastFurnaceRecipe(Items.INGOT_REFINED_IRON, Items.CLUMP_ORE_IRON));
        //Maybe a quicker burntime? Maybe add normal furnace recipes if no matching blastfurnacerecipe is found?
        BlastFurnaceRecipe.add(new BlastFurnaceRecipe(Items.INGOT_REFINED_IRON, Items.INGOT_IRON));
    }
}
