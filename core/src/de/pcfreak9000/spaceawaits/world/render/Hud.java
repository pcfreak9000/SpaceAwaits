package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.menu.HotbarSlot;
import de.pcfreak9000.spaceawaits.menu.ScreenManager;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;

public class Hud {
    private static final int HOTBAR_SLOTS = 9;
    
    private Stage stage;
    
    private ScreenManager gsm;
    
    private HotbarSlot[] slots;
    private int selectedSlot;
    
    public Hud(ScreenManager gsm) {
        this.gsm = gsm;
        this.stage = gsm.createStage();
        initHotbarSlots();
        Table table = new Table(gsm.getSkin());
        table.align(Align.top);
        table.add(createHotbarSlotsTable()).pad(10);
        table.setWidth(stage.getWidth());
        table.setHeight(stage.getHeight());
        this.stage.addActor(table);
        selectSlot(0);
        slots[4].setItemStack(new ItemStack(GameRegistry.ITEM_REGISTRY.get("gun"), HOTBAR_SLOTS));
    }
    
    private void initHotbarSlots() {
        slots = new HotbarSlot[HOTBAR_SLOTS];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new HotbarSlot();
        }
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
