package de.pcfreak9000.spaceawaits.item;

import de.pcfreak9000.nbt.NBTCompound;

public class ItemHelper {
    //move to some bar component in item?
    public static void dealDamageUpdateBar(ItemStack stack, int dmgDealt, int max, boolean removeIfUsedUp) {
        NBTCompound nbt = stack.getOrCreateNBT();
        nbt.putInt("barMax", max);
        nbt.putInt("bar", nbt.getIntOrDefault("bar", max) - dmgDealt);
        if (removeIfUsedUp && nbt.getInt("bar") <= 0) {
            stack.changeNumber(-1);
        }
    }
    
    public static ItemStack getNBTStoredItemStack(ItemStack container, String compId) {
        if (container.hasNBT() && container.getNBT().hasKey(compId)) {
            NBTCompound ensrccomp = container.getNBT().getCompound(compId);
            ItemStack ensrc = ItemStack.readNBT(ensrccomp);
            return ensrc;
        }
        return null;
    }
    
    public static void setNBTStoredItemStack(ItemStack container, String compId, ItemStack stack) {
        if (!ItemStack.isEmptyOrNull(stack)) {
            NBTCompound old = null;
            if (container.hasNBT() && container.getNBT().hasKey(compId)) {
                old = container.getNBT().getCompound(compId);
                old.removeAll();
            } else {
                old = new NBTCompound();
            }
            NBTCompound comp = ItemStack.writeNBT(stack, old);
            container.getOrCreateNBT().putCompound(compId, comp);
        } else if (container.hasNBT() && container.getNBT().hasKey(compId)) {
            container.getNBT().remove(compId);
        }
    }
    
}
