package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.core.assets.ITextureProvider;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.module.ModuleBar;

public class ActorItemStack extends Actor {

    private static final float BAR_HEIGHT = 1.5f;

    private ItemStack itemstack;
    public boolean drawcount = true;// Hmmm

    private Label label;

    public ActorItemStack() {
        this.label = new Label("", CoreRes.SKIN.getSkin());
        this.label.setFontScale(0.7f);
    }

    public void setItemStack(ItemStack stack) {
        this.itemstack = stack;
    }

    public boolean hasStack() {
        return itemstack != null;
    }

    public ItemStack getItemStack() {
        return this.itemstack;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!ItemStack.isEmptyOrNull(itemstack)) {
            Item i = itemstack.getItem();
            ITextureProvider t = i.getIcon();
            batch.setColor(i.getColor());
            batch.draw(t.getRegion(), getX(), getY(), getWidth(), getHeight());
            if (itemstack.getItem().hasModule(ModuleBar.ID)) {
                ModuleBar bar = itemstack.getItem().getModule(ModuleBar.ID);
                float fill = bar.getValue(itemstack);
                if (fill != ModuleBar.VALUE_HIDE) {
                    float widthFull = getWidth() * 0.9f;
                    float pad = (getWidth() - widthFull) * 0.5f;
                    batch.setColor(bar.getColorBack(itemstack));
                    batch.draw(CoreRes.WHITE, getX() + pad, getY() - BAR_HEIGHT * 1.1f, widthFull, BAR_HEIGHT);
                    batch.setColor(bar.getColorFill(itemstack));
                    batch.draw(CoreRes.WHITE, getX() + pad, getY() - BAR_HEIGHT * 1.1f, fill * widthFull, BAR_HEIGHT);
                }
            }
            if (itemstack.getCount() > 1 && drawcount) {
                this.label.setText(itemstack.getCount() + "");
                this.label.setPosition(getX(), getY() + getHeight() * 0.85f);
                this.label.draw(batch, 1f);
            }
        }
    }
}
