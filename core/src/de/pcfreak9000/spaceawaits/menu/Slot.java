package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.item.IInventory;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class Slot extends Actor {
    private static final float SIZE = 40;
    
    protected final IInventory inventoryBacking;
    protected final int slotIndex;
    
    private ClickListener listener;
    
    public Slot(IInventory invBacking, int index) {
        this.inventoryBacking = invBacking;
        this.slotIndex = index;
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
        if (itemstack != null && !itemstack.isEmpty()) {
            Item i = itemstack.getItem();
            ITextureProvider t = i.getTextureProvider();
            float wt = getWidth() * 0.15f;
            float ht = getHeight() * 0.15f;
            batch.setColor(i.color());
            batch.draw(t.getRegion(), getX() + wt, getY() + ht, getWidth() * 0.7f, getHeight() * 0.7f);
            CoreRes.FONT.draw(batch, itemstack.getCount() + "", getX(), getY() + getHeight());
            //render item and item count
        }
        if (listener.isOver()) {
            batch.setColor(1, 1, 1, 0.2f);
            batch.draw(CoreRes.WHITE, getX(), getY(), getWidth(), getHeight());
        }
        batch.setColor(old);
    }
    
}
