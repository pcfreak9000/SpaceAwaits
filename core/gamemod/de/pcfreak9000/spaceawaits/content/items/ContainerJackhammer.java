package de.pcfreak9000.spaceawaits.content.items;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.gui.GuiInventory;
import de.pcfreak9000.spaceawaits.gui.Slot;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class ContainerJackhammer extends GuiInventory {
    
    private static final String COMPOUNDID = "stackEnergySrc";
    
    private ItemStack hammer;
    
    private InventoryJackhammer inv;
    
    public ContainerJackhammer(ItemStack hammer) {
        this.hammer = hammer;
        this.inv = new InventoryJackhammer();
        if (hammer.hasNBT() && hammer.getNBT().hasKey(COMPOUNDID)) {
            NBTCompound ensrccomp = hammer.getNBT().getCompound(COMPOUNDID);
            ItemStack ensrc = ItemStack.readNBT(ensrccomp);
            inv.setSlotContent(0, ensrc);
        }
    }
    
    @Override
    protected void create() {
        super.create();
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table subtable = new Table();
        subtable.align(Align.center);
        Label label = new Label("Jackhammer", CoreRes.SKIN.getSkin());
        supertable.add(label).pad(1f);
        supertable.row();
        subtable.add(registerSlot(new Slot(inv, 0)));
        supertable.add(subtable).pad(10f);
        supertable.row();
        supertable.add(createPlayerInventoryTable());
        stage.addActor(supertable);
    }
    
    @Override
    public void onClosed() {
        super.onClosed();
        if (!ItemStack.isEmptyOrNull(inv.getStack(0))) {
            NBTCompound old = null;
            if (hammer.getNBT().hasKey(COMPOUNDID)) {
                old = hammer.getNBT().getCompound(COMPOUNDID);
                old.removeAll();
            } else {
                old = new NBTCompound();
            }
            NBTCompound comp = ItemStack.writeNBT(inv.getStack(0), old);
            hammer.getOrCreateNBT().putCompound(COMPOUNDID, comp);
        } else {
            hammer.getOrCreateNBT().remove(COMPOUNDID);
        }
    }
}
