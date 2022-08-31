package de.pcfreak9000.spaceawaits.crafting;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;

public class CraftingManager {
    
    private static final CraftingManager INSTANCE = new CraftingManager();
    
    public static CraftingManager instance() {
        return INSTANCE;
    }
    
    private Array<SimpleRecipe> simpleRecipes = new Array<>();
    private Array<ShapedRecipe> shapedRecipes = new Array<>();
    
    public void addSimpleRecipe(ItemStack result, ItemStack... inputs) {
        simpleRecipes.add(new SimpleRecipe(result, inputs));
    }
    
    public void addShapedRecipe(ShapedRecipe sr) {
        this.shapedRecipes.add(sr);
    }
    
    public IRecipe findMatchingRecipe(InventoryCrafting inventoryCrafting) {
        for (ShapedRecipe s : shapedRecipes) {
            if (s.matches(inventoryCrafting)) {
                return s;
            }
        }
        return null;
    }
    
    public Array<SimpleRecipe> getRecipesSimple() {
        return simpleRecipes;
    }
    
}
