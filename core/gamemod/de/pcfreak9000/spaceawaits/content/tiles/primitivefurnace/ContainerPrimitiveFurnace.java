package de.pcfreak9000.spaceawaits.content.tiles.primitivefurnace;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.gui.GuiInventory;
import de.pcfreak9000.spaceawaits.gui.ResultSlot;
import de.pcfreak9000.spaceawaits.gui.Slot;

public class ContainerPrimitiveFurnace extends GuiInventory {
    
    private TileEntityPrimitiveFurnace furnace;
    
    private ProgressBar pb;
    
    public ContainerPrimitiveFurnace(TileEntityPrimitiveFurnace te) {
        this.furnace = te;
    }
    
    @Override
    protected void create() {
        super.create();
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table some = new Table();
        Table subtable = new Table();
        subtable.align(Align.center);
        subtable.add(registerSlot(new Slot(furnace, TileEntityPrimitiveFurnace.INPUTSLOT))).pad(5f);
        subtable.row();
        subtable.add(registerSlot(new Slot(furnace, TileEntityPrimitiveFurnace.FUELSLOT))).pad(5f);
        some.add(subtable);
        pb = new ProgressBar(0, 1f, 0.00001f, false, CoreRes.SKIN.getSkin());
        some.add(pb).pad(5f);
        some.add(registerSlot(new ResultSlot(furnace, TileEntityPrimitiveFurnace.RESULTSLOT)));
        supertable.add(some).pad(10f);
        supertable.row();
        supertable.add(createPlayerInventoryTable());
        stage.addActor(supertable);
    }
    
    @Override
    public void actAndDraw(float dt) {
        super.actAndDraw(dt);
        pb.setValue(furnace.getRelativeProgress());//Why the fuck does the progressbar show stuff when the value is 0??????!!
    }
}
