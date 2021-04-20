package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.item.InventoryPlayer;
import de.pcfreak9000.spaceawaits.menu.HotbarSlot;
import de.pcfreak9000.spaceawaits.menu.ScreenManager;

public class Hud {
    
    private Stage stage;
    
    private ScreenManager gsm;
    private Player player;
    
    private HotbarSlot[] slots;
    private int selectedSlot;
    
    public Hud(ScreenManager gsm) {
        this.gsm = gsm;
        this.stage = gsm.createStage();

    }
    
    public void setPlayer(Player player) {
        this.player = player;
        this.initHotbarSlots();
        this.selectSlot(0);
    }
    //FIXME this sucks ^ V
    private void initHotbarSlots() {
        InventoryPlayer inv = this.player.getInventory();
        slots = new HotbarSlot[inv.slots()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new HotbarSlot(inv, i);
        }
        Table table = new Table(gsm.getSkin());
        table.align(Align.top);
        table.add(createHotbarSlotsTable()).pad(10);
        table.setWidth(stage.getWidth());
        table.setHeight(stage.getHeight());
        this.stage.addActor(table);
    }
    
    private Table createHotbarSlotsTable() {
        Table table = new Table();
        for (HotbarSlot hs : slots) {
            table.add(hs).pad(5);
        }
        return table;
    }
    
    private void selectSlot(int newselection) {
        if (newselection != selectedSlot) {
            slots[selectedSlot].setSelected(false);
            slots[newselection].setSelected(true);
            selectedSlot = newselection;
        }
    }
    
    public void actAndDraw(float dt) {
        this.gsm.actAndDraw(stage, dt);
    }
    
    public void dispose() {
        this.stage.dispose();
    }
}
