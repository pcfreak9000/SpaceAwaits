package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.Player;
import de.pcfreak9000.spaceawaits.item.InventoryPlayer;
import de.pcfreak9000.spaceawaits.menu.GuiHelper;
import de.pcfreak9000.spaceawaits.menu.HotbarSlot;
import de.pcfreak9000.spaceawaits.world.ecs.HealthComponent;

public class Hud {
    
    public Stage stage;
    
    private GuiHelper guiHelper;
    private Player player;
    
    private HotbarSlot[] slots;
    private ProgressBar healthbar;
    
    public Hud(GuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.stage = guiHelper.createStage();
    }
    
    public void setPlayer(Player player) {
        this.player = player;
        this.initHotbarSlots();
        //        ProgressBar b = new ProgressBar(0, 1, 0.1f, false, CoreRes.SKIN.getSkin());
        //        Table t = new Table(CoreRes.SKIN.getSkin());
        //        t.setFillParent(true);
        //        t.align(Align.topLeft);
        //        t.add(b).pad(10);
        //        b.setAnimateDuration(3);
        //        b.setAnimateInterpolation(Interpolation.smooth);
        //        b.setValue(1);
        //        stage.addActor(t);
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
        table.setFillParent(true);
        healthbar = new ProgressBar(0, 1, 0.01f, false, CoreRes.SKIN.getSkin());
        healthbar.setAnimateInterpolation(Interpolation.fade);
        healthbar.setAnimateDuration(0.1f);
        table.add(healthbar).expandX().top().left().pad(15);
        table.add(createHotbarSlotsTable()).padTop(10).top().center();
        table.add().expandX().top().right().pad(10).prefSize(healthbar.getPrefWidth(), healthbar.getPrefHeight());
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
        HealthComponent playerHealth = player.getPlayerEntity().getComponent(HealthComponent.class);
        this.healthbar.setValue(playerHealth.currentHealth / playerHealth.maxHealth);
        this.guiHelper.actAndDraw(stage, dt);
    }
    
    public void dispose() {
        this.stage.dispose();
    }
}
