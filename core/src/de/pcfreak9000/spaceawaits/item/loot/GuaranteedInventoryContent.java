package de.pcfreak9000.spaceawaits.item.loot;

import java.util.Random;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class GuaranteedInventoryContent {
    
    private ItemStack itemstack;
    private int minCount;
    private int maxCount;
    
    public GuaranteedInventoryContent(Item item, int min, int max) {
        this.itemstack = new ItemStack(item, 1);
        this.minCount = min;
        this.maxCount = max;
    }
    
    public ItemStack[] generate(Random rand) {
        return WeightedRandomInventoryContent.generateStacks(rand, itemstack, minCount, maxCount);
    }
    
}
