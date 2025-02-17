package de.pcfreak9000.spaceawaits.core;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.gui.GuiInventory;
import de.pcfreak9000.spaceawaits.gui.Slot;
import de.pcfreak9000.spaceawaits.item.IInventory;

public class ContainerTesting extends GuiInventory {
    
    private static class TestingSlot extends Slot {
        
        public TestingSlot(IInventory invBacking, int index, boolean trash) {
            super(invBacking, index);
            actorItemStack.drawcount = false;
            canPut = trash;
            canTake = !trash;
        }
        
    }
    
    private InventoryTesting inv;
    
    public ContainerTesting() {
        this.inv = new InventoryTesting();
    }
    
    @Override
    protected void create() {
        super.create();
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table subtable = new Table();
        subtable.align(Align.center);
        Label label = new Label("Testing", CoreRes.SKIN.getSkin());
        supertable.add(label).pad(1f);
        supertable.row();
        for (int i = 0; i < inv.slots() - 1; i++) {
            if (i % 9 == 0) {
                subtable.row();
            }
            subtable.add(registerSlot(new TestingSlot(inv, i, false))).pad(0.5f);
        }
        subtable.add(registerSlot(new TestingSlot(inv, inv.slots() - 1, true)));
        supertable.add(subtable).pad(10f);
        supertable.row();
        supertable.add(createPlayerInventoryTable());
        stage.addActor(supertable);
    }
}
