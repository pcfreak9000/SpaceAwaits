package de.pcfreak9000.spaceawaits.content;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.content.entities.Entities;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.content.tiles.Tiles;
import de.pcfreak9000.spaceawaits.core.CoreEvents;
import de.pcfreak9000.spaceawaits.crafting.FurnaceRecipe;
import de.pcfreak9000.spaceawaits.crafting.ShapedRecipe;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.mod.Mod;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;

@Mod(id = "SpaceAwaits-Game", name = "Space Awaits Main Game", version = { 0, 0, 1 })
public class GameMod {
    @EventSubscription
    public void init(final CoreEvents.InitEvent init) {
        //Components.registerComponents();
        Items.registerItems();
        Tiles.registerTiles();
        Entities.registerEntities();
        GameRegistry.registerBurnHandler(new BurnHandler());
    }
    
    @EventSubscription
    public void postinit(CoreEvents.PostInitEvent ev) {
        ShapedRecipe.add(new ShapedRecipe(Tiles.FURNACE_PRIMITIVE, " X ", "X X", "XXX", 'X', Tiles.STONE));
        ShapedRecipe.add(new ShapedRecipe(new ItemStack(Items.STICK, 4), "X", "X", 'X', Tiles.WOOD));
        FurnaceRecipe.add(new FurnaceRecipe(Items.INGOT_IRON, Items.CLUMP_ORE_IRON));
    }
}
