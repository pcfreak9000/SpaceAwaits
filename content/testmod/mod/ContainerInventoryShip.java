package mod;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.gui.GuiInventory;
import de.pcfreak9000.spaceawaits.gui.Slot;

public class ContainerInventoryShip extends GuiInventory {
    
    private final InventoryShip invShip;
    
    public ContainerInventoryShip(InventoryShip invShip) {
        super(invShip);
        this.invShip = invShip;
    }
    
    @Override
    protected void create() {
        super.create();
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table subtable = new Table();
        subtable.align(Align.center);
        Label label = new Label("Emergency Pack", CoreRes.SKIN.getSkin());
        supertable.add(label).pad(1f);
        supertable.row();
        for (int i = 0; i < invShip.slots(); i++) {
            subtable.add(registerSlot(new Slot(invShip, i))).pad(0.5f);
        }
        supertable.add(subtable).pad(10f);
        supertable.row();
        supertable.add(createPlayerInventoryTable());
        stage.addActor(supertable);
    }
}
