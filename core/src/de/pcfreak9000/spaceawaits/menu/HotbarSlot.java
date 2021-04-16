package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import de.pcfreak9000.spaceawaits.core.CoreResources;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class HotbarSlot extends Actor {
    
    private static final float SIZE = 40;
    
    private ItemStack itemstack;
    
    public HotbarSlot() {
        setSize(SIZE, SIZE);
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(CoreResources.SPACE_BACKGROUND.getRegion(), getX(), getY(), getWidth(), getHeight());
        if (itemstack != null) {
            //render item and item count
        }
    }
    
    public void setItemStack(ItemStack stack) {//Hmmmm
        this.itemstack = stack;
    }
}
