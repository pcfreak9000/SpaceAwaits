package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

import de.pcfreak9000.spaceawaits.core.CoreResources;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.item.InventoryPlayer;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class HotbarSlot extends Slot {
    
    private static final float UNSELECTED_BIAS = 0.6f;
    
    private boolean selected;
    
    public HotbarSlot(InventoryPlayer inv, int index) {
        super(inv, index);
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color old = batch.getColor();
        Color c = getColor();
        float bias = bias();
        batch.setColor(c.r * bias, c.g * bias, c.b * bias, c.a);
        batch.draw(CoreResources.SPACE_BACKGROUND.getRegion(), getX(), getY(), getWidth(), getHeight());
        ItemStack itemstack = inventoryBacking.getStack(slotIndex);
        if (itemstack != null) {
            Item i = itemstack.getItem();
            ITextureProvider t = i.getTextureProvider();
            float wt = getWidth() * 0.1f;
            float ht = getHeight() * 0.1f;
            Color ic = i.color();
            batch.setColor(ic.r * bias, ic.g * bias, ic.b * bias, ic.a);
            batch.draw(t.getRegion(), getX() + wt, getY() + ht, getWidth() * 0.8f, getHeight() * 0.8f);
            //render item and item count
        }
        batch.setColor(old);
    }
    
    private float bias() {
        return (selected ? 1 : UNSELECTED_BIAS);
    }
    
    public void setSelected(boolean b) {
        this.selected = b;
    }
}
