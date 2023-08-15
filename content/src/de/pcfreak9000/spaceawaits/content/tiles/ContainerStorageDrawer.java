package de.pcfreak9000.spaceawaits.content.tiles;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.gui.GuiInventory;
import de.pcfreak9000.spaceawaits.gui.Slot;

public class ContainerStorageDrawer extends GuiInventory {
    
    private final TileEntityStorageDrawer te;
    
    public ContainerStorageDrawer(TileEntityStorageDrawer te) {
        super(te);
        this.te = te;
    }
    
    @Override
    protected void create() {
        super.create();
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table subtable = new Table();
        subtable.align(Align.center);
        Label label = new Label("Storage Drawer", CoreRes.SKIN.getSkin());
        supertable.add(label).pad(1f);
        supertable.row();
        for (int i = 0; i < te.slots(); i++) {
            if (i % 9 == 0) {
                subtable.row();
            }
            subtable.add(registerSlot(new Slot(te, i))).pad(0.5f);
        }
        supertable.add(subtable).pad(10f);
        supertable.row();
        supertable.add(createPlayerInventoryTable());
        stage.addActor(supertable);
    }
}
