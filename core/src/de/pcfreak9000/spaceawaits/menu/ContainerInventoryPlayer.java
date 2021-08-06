package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.item.InventoryPlayer;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

public class ContainerInventoryPlayer extends GuiInventory {
    
    public ContainerInventoryPlayer(GameRenderer renderer, InventoryPlayer inv) {
        super(renderer);
        Table table = new Table();
        table.setFillParent(true);
        table.align(Align.center);
        for (int i = 9; i < inv.slots(); i++) {
            if (i % 9 == 0) {
                table.row();
            }
            table.add(createSlot(inv, i));
        }
        table.row();
        for (int i = 0; i < 9; i++) {
            table.add(createSlot(inv, i)).padTop(10);
        }
        stage.addActor(table);
        //renderer.getGuiHelper().showDialog("T", "Dies ist Test", stage);
    }
    
}
