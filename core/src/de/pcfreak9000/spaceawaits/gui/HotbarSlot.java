package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.InventoryPlayer;

public class HotbarSlot extends Slot {
    
    public HotbarSlot(InventoryPlayer inv, int index) {
        super(inv, index);
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color old = batch.getColor();
        batch.setColor(getColor());
        batch.draw(CoreRes.ITEM_SLOT.getRegion(), getX(), getY(), getWidth(), getHeight());
        ItemStack itemstack = inventoryBacking.getStack(slotIndex);
        layoutActorItemStack();
        this.actorItemStack.setItemStack(itemstack);
        this.actorItemStack.draw(batch, parentAlpha);
        if (isSelected()) {
            drawSlotHighlight(batch);
        }
        batch.setColor(old);
    }
    
    private boolean isSelected() {
        return ((InventoryPlayer) inventoryBacking).getSelectedSlot() == slotIndex;
    }
}
