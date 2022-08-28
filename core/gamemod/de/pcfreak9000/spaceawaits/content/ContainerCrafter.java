package de.pcfreak9000.spaceawaits.content;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.gui.GuiInventory;
import de.pcfreak9000.spaceawaits.gui.Slot;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class ContainerCrafter extends GuiInventory {
    
    private final int side;
    private final InventoryCrafting inv;
    
    public ContainerCrafter(int side) {
        this.side = side;
        this.inv = new InventoryCrafting(side);
    }
    
    @Override
    protected void create() {
        super.create();
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table subtable = new Table();
        subtable.align(Align.center);
        for (int i = 0; i < inv.slots(); i++) {
            if (i % side == 0) {
                subtable.row();
            }
            subtable.add(registerSlot(new Slot(inv, i))).pad(0.5f);
        }
        supertable.add(subtable).pad(10f);
        supertable.row();
        supertable.add(createPlayerInventoryTable());
        stage.addActor(supertable);
    }
    
    @Override
    public void onClosed() {
        super.onClosed();
        for (int i = 0; i < inv.slots(); i++) {
            ItemStack stack = inv.removeStack(i);
            if (!ItemStack.isEmptyOrNull(stack)) {
                ItemStack leftover = InvUtil.insert(player.getInventory(), stack);
                player.dropWhenPossible(leftover);
            }
        }
    }
}
