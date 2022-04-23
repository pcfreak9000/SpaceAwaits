package mod;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.gui.GuiInventory;
import de.pcfreak9000.spaceawaits.gui.Slot;

public class ContainerCrafter extends GuiInventory {
    
    private final int side;
    
    public ContainerCrafter(int side) {
        this.side = side;
    }
    
    @Override
    protected void create() {
        super.create();
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table subtable = new Table();
        subtable.align(Align.center);
        for (int i = 0; i < side*side; i++) {
            if (i % side == 0) {
                subtable.row();
            }
            subtable.add(registerSlot(new Slot(player.getInventory(), i))).pad(0.5f);
        }
        supertable.add(subtable).pad(10f);
        supertable.row();
        supertable.add(createPlayerInventoryTable());
        stage.addActor(supertable);
    }
}
