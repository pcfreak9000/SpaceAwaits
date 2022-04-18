package de.pcfreak9000.spaceawaits.item.loot;

import java.util.Random;

import de.omnikryptec.math.Mathf;
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
        this.usedUp = usedUp;
    }
    
    public WeightedRandomInventoryContent(int weight, boolean usedUp) {
        this.itemstack = null;
        this.weight = weight;
        this.usedUp = usedUp;
        this.minCount = 0;
        this.maxCount = 0;
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
        if (source == null || source.getItem() == null) {
            ret = new ItemStack[0];
        } else if (count > source.getMax()) {
            int stackcount = Mathf.ceili(count / (float) source.getMax());//3
            int countperstack = Mathf.ceili(count / (float) stackcount);//2
            ret = new ItemStack[stackcount];
            for (int x = 0; x < ret.length; x++) {
                ret[x] = source.cpy();
                ret[x].setCount(count < countperstack ? count : countperstack);
                count -= countperstack;
            }
        } else {
            ret = new ItemStack[1];
            ret[0] = source.cpy();
            ret[0].setCount(count);
        }
        return ret;
    }
}
