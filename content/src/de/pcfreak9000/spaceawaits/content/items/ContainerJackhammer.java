package de.pcfreak9000.spaceawaits.content.items;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.gui.GuiInventory;
import de.pcfreak9000.spaceawaits.gui.Slot;
import de.pcfreak9000.spaceawaits.item.ItemHelper;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class ContainerJackhammer extends GuiInventory {
    
    public static final String COMPOUNDID = "stackEnergySrc";
    
    private ItemStack hammer;
    
    private InventoryJackhammer inv;
    
    public ContainerJackhammer(ItemStack hammer) {
        this.hammer = hammer;
        this.inv = new InventoryJackhammer();
        inv.setSlotContent(0, ItemHelper.getNBTStoredItemStack(hammer, COMPOUNDID));
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
        ItemHelper.setNBTStoredItemStack(hammer, COMPOUNDID, inv.getStack(0));
    }
}
