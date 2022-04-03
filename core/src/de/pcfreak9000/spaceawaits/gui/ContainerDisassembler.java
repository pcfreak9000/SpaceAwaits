package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.CoreRes;

public class ContainerDisassembler extends GuiInventory {
    
    @Override
    protected void create() {
        super.create();
        DisassemblerInventory dinv = new DisassemblerInventory();
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table subtable = new Table();
        subtable.align(Align.center);
        subtable.add(registerSlot(new Slot(dinv, 0))).pad(1f);
        TextButton b = new TextButton("Disassemble", CoreRes.SKIN.getSkin());
        b.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                dinv.removeStack(0);
            }
        });
        subtable.add(b).pad(1f);
        supertable.add(subtable).pad(10f);
        supertable.row();
        supertable.add(createPlayerInventoryTable());
        stage.addActor(supertable);
    }
    
}
