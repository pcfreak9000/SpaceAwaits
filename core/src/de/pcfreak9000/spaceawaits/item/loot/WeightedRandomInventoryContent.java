package de.pcfreak9000.spaceawaits.item.loot;

import java.util.Random;

import de.omnikryptec.math.Weighted;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class WeightedRandomInventoryContent implements Weighted {
    
    private int weight;
    private ItemStack itemstack;
    private int minCount;
    private int maxCount;
    private boolean usedUp;
    
    public WeightedRandomInventoryContent(Item item, int weight, int min, int max, boolean usedUp) {
        this.itemstack = new ItemStack(item, 1);
        this.weight = weight;
        this.minCount = min;
        this.maxCount = max;
    }
    
    @Override
    public int getWeight() {
        return weight;
    }
    
    public boolean isUsedUp() {
        return usedUp;
    }
    
    public ItemStack[] generate(Random rand) {
        return generateStacks(rand, itemstack, minCount, maxCount);
    }
    
    public static ItemStack[] generateStacks(Random rand, ItemStack source, int min, int max) {
        int count = min + (rand.nextInt(max - min + 1));
        
        ItemStack[] ret;
        if (source.getItem() == null) {
            ret = new ItemStack[0];
        } else {
            ret = new ItemStack[1];
            ret[0] = source.cpy();
            ret[0].setCount(count);
        }
        return ret;
    }
}
