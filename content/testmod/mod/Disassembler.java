package mod;
import com.badlogic.ashley.utils.ImmutableArray;

import de.pcfreak9000.spaceawaits.comp.Composite;
import de.pcfreak9000.spaceawaits.comp.CompositeData;
import de.pcfreak9000.spaceawaits.comp.CompositeInventory;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class Disassembler {
    private int level;
    
    public Disassembler(int level) {
        this.level = level;
    }
    
    public boolean canDisassemble(ItemStack item) {
        if (ItemStack.isEmptyOrNull(item)) {
            return false;
        }
        return item.getItem().getComposite() != null && item.getItem().getComposite().getLevel() > level;
    }
    
    public void disassemble(ItemStack item, CompositeInventory compInv) {
        int count = item.getCount();
        Composite comp = item.getItem().getComposite();
        disassemble(comp, count, compInv);
        compInv.print();
    }
    
    private void disassemble(Composite top, float count, CompositeInventory compInv) {
        if (top.getLevel() <= level) {
            compInv.putComposite(top, count);
        } else {
            ImmutableArray<CompositeData> compData = top.getContents();
            for (CompositeData cd : compData) {
                disassemble(cd.composite, cd.amount * count, compInv);
            }
        }
    }
}
