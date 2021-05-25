package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class Slot extends Actor {
    static final float SIZE = 40;
    
    protected final IInventory inventoryBacking;
    protected final int slotIndex;
    
    protected final ActorItemStack actorItemStack;
    
    private ClickListener listener;
    
    public Slot(IInventory invBacking, int index) {
        this.inventoryBacking = invBacking;
        this.slotIndex = index;
        this.actorItemStack = new ActorItemStack();
        this.actorItemStack.setSize(SIZE, SIZE);
        this.listener = new ClickListener();
        addListener(listener);
        setSize(SIZE, SIZE);
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
        if (highlightSlot()) {
            drawSlotHighlight(batch);
        }
        batch.setColor(old);
    }
    
    protected void layoutActorItemStack() {
        final float offset = 0.1f;
        float wt = getWidth() * offset;
        float ht = getHeight() * offset;
        this.actorItemStack.setBounds(getX() + wt, getY() + ht, getWidth() * (1 - offset * 2),
                getHeight() * (1 - offset * 2));
    }
    
    protected void drawSlotHighlight(Batch batch) {
        batch.setColor(1, 1, 1, 0.2f);
        batch.draw(CoreRes.WHITE, getX(), getY(), getWidth(), getHeight());
    }
    
    protected boolean highlightSlot() {
        return listener.isOver();
    }
    
    public ItemStack getStack() {
        return inventoryBacking.getStack(slotIndex);
    }
    
}
