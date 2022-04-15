package de.pcfreak9000.spaceawaits.item.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;

import de.omnikryptec.math.MathUtil;
import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class LootTable {
    
    private static ObjectMap<String, LootTable> tables = new ObjectMap<>();
    
    public static LootTable getFor(String name) {
        LootTable lt = tables.get(name);
        if (lt == null) {
            lt = new LootTable();
            tables.put(name, lt);
        }
        return lt;
    }
    
    private ArrayList<WeightedRandomInventoryContent> loot = new ArrayList<>();
    private ArrayList<GuaranteedInventoryContent> loot2 = new ArrayList<>();
    private int minItems, maxItems;
    
    private LootTable() {
    }
    
    public void addMin(int min) {
        this.minItems += min;
    }
    
    public void addMax(int max) {
        this.maxItems += max;
    }
    
    public int getMin() {
        return minItems;
    }
    
    public int getMax() {
        return maxItems;
    }
    
    public void add(WeightedRandomInventoryContent wric) {
        loot.add(wric);
    }
    
    public void add(GuaranteedInventoryContent mic) {
        loot2.add(mic);
    }
    
    public void generate(Random random, IInventory inv) {
        int count = minItems + random.nextInt(maxItems - minItems + 1);
        List<WeightedRandomInventoryContent> loot = new ArrayList<>(this.loot);
        for (int i = 0; i < count; i++) {
            if (loot.isEmpty()) {
                break;
            }
            WeightedRandomInventoryContent type = MathUtil.getWeightedRandom(random, loot);
            if (type.isUsedUp()) {
                loot.remove(type);
            }
            ItemStack[] stacks = type.generate(random);
            IntArray emptySlots = InvUtil.findEmptySlots(inv);
            for (ItemStack st : stacks) {
                int index = emptySlots.isEmpty() ? random.nextInt(inv.slots())
                        : emptySlots.removeIndex(random.nextInt(emptySlots.size));
                inv.setSlotContent(index, st);
            }
        }
        for (GuaranteedInventoryContent mic : loot2) {
            ItemStack[] stacks = mic.generate(random);
            IntArray emptySlots = InvUtil.findEmptySlots(inv);
            for (ItemStack st : stacks) {
                int index = emptySlots.isEmpty() ? random.nextInt(inv.slots())
                        : emptySlots.removeIndex(random.nextInt(emptySlots.size));
                inv.setSlotContent(index, st);
            }
        }
    }
}
