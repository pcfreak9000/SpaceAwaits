package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.item.InventoryPlayer;
import de.pcfreak9000.spaceawaits.menu.GuiContainer;
import de.pcfreak9000.spaceawaits.menu.Slot;

public class ContainerInventoryPlayer extends GuiContainer {
    
    public ContainerInventoryPlayer(GameRenderer renderer, InventoryPlayer inv) {
        super(renderer);
        Table table = new Table();
        table.setWidth(stage.getWidth());
        table.setHeight(stage.getHeight());
        table.align(Align.center);
        for (int i = 0; i < inv.slots(); i++) {
            table.add(new Slot(inv, i));
        }
        stage.addActor(table);
        //renderer.getGuiHelper().showDialog("T", "Dies ist Test", stage);
    }
    
}
