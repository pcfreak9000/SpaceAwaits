package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ContainerInventoryPlayer extends GuiInventory {
    
    @Override
    protected void create() {
        super.create();
        Table t = createPlayerInventoryTable();
        t.setFillParent(true);
        stage.addActor(t);
    }
    
}
