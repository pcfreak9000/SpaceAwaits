package de.pcfreak9000.spaceawaits.crafting;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.ItemStack;

public class CraftingManager {
    
    private static final CraftingManager INSTANCE = new CraftingManager();
    
    public static CraftingManager instance() {
        return INSTANCE;
    }
    
    private Array<SimpleRecipe> simpleRecipes = new Array<>();
    
    public void addSimpleRecipe(ItemStack result, ItemStack... inputs) {
        simpleRecipes.add(new SimpleRecipe(result, inputs));
    }
    
    public Array<SimpleRecipe> getRecipesSimple() {
        return simpleRecipes;
    }
}
