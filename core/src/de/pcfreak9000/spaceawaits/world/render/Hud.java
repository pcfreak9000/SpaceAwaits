package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.item.InventoryPlayer;
import de.pcfreak9000.spaceawaits.menu.GuiHelper;
import de.pcfreak9000.spaceawaits.menu.HotbarSlot;

public class Hud {
    
    public Stage stage;
    
    private GuiHelper guiHelper;
    private Player player;
    
    private HotbarSlot[] slots;
    
    public Hud(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.stage = guiHelper.createStage();
    }
    
    public void setPlayer(Player player) {
        this.player = player;
        this.initHotbarSlots();
    }
    
    //FIXME this sucks ^ V
    private void initHotbarSlots() {
        InventoryPlayer inv = this.player.getInventory();
        slots = new HotbarSlot[9];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new HotbarSlot(inv, i);
        }
        Table table = new Table(CoreRes.SKIN.getSkin());
        table.align(Align.top);
        table.add(createHotbarSlotsTable()).pad(10);
        table.setFillParent(true);
        this.stage.clear();//For now, fixes a bug. also clears listeners
        this.stage.addActor(table);
    }
    
    private Table createHotbarSlotsTable() {
        Table table = new Table();
        for (HotbarSlot hs : slots) {
            table.add(hs).pad(5);
        }
        return table;
    }
    
    public void actAndDraw(float dt) {
        this.guiHelper.actAndDraw(stage, dt);
    }
    
    public void dispose() {
        this.stage.dispose();
    }
}
