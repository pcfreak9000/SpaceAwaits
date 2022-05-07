package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class ActorItemStack extends Actor {
    
    private ItemStack itemstack;
    
    public void setItemStack(ItemStack stack) {
        this.itemstack = stack;
    }
    
    public boolean hasStack() {
        return itemstack != null;
    }
    
    public ItemStack getItemStack() {
        return this.itemstack;
    }
    
    private static final float BAR_HEIGHT = 2f;
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (itemstack != null && !itemstack.isEmpty()) {
            Item i = itemstack.getItem();
            ITextureProvider t = i.getTextureProvider();
            batch.setColor(i.color());
            batch.draw(t.getRegion(), getX(), getY(), getWidth(), getHeight());
            if (itemstack.getNBT() != null) {
                if (itemstack.getNBT().hasKey("bar")) {//FIXME this stinks
                    float fill = itemstack.getNBT().getFloat("bar");
                    float max = getWidth() * 0.9f;
                    float pad = (getWidth() - max) * 0.5f;
                    batch.setColor(Color.RED);
                    batch.draw(CoreRes.WHITE, getX() + pad, getY(), max, BAR_HEIGHT);
                    batch.setColor(Color.GREEN);
                    batch.draw(CoreRes.WHITE, getX() + pad, getY(), fill * max, BAR_HEIGHT);
                }
            }
            if (itemstack.getCount() > 1) {
                CoreRes.FONT.draw(batch, itemstack.getCount() + "", getX(), getY() + getHeight());
            }
        }
    }
}
