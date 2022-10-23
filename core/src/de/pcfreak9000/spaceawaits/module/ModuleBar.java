package de.pcfreak9000.spaceawaits.module;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import de.pcfreak9000.spaceawaits.item.ItemStack;

public class ModuleBar implements IModule {
    
    public static final ModuleID ID = ModuleID.getFor(ModuleBar.class);
    public static final float VALUE_HIDE = -1f;
    
    public void setValue(ItemStack stack, float f) {
        stack.getOrCreateNBT().putFloat("barValue", MathUtils.clamp(f, 0, 1f));
    }
    
    public float getValue(ItemStack stack) {
        if (!stack.hasNBT() || !stack.getNBT().hasKey("barValue")) {
            return VALUE_HIDE;
        }
        return stack.getNBT().getFloat("barValue");
    }
    
    public Color getColorBack(ItemStack stack) {
        return Color.RED;
    }
    
    public Color getColorFill(ItemStack stack) {
        return Color.GREEN;
    }
}
