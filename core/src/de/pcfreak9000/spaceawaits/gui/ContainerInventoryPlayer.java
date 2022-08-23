package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.crafting.CraftingManager;
import de.pcfreak9000.spaceawaits.crafting.SimpleRecipe;
import de.pcfreak9000.spaceawaits.item.InvUtil;
import de.pcfreak9000.spaceawaits.item.ItemStack;

public class ContainerInventoryPlayer extends GuiInventory {
    
    private static class RecipeUI {
        SimpleRecipe simpleRecipe;
        
        @Override
        public String toString() {
            return simpleRecipe.getResult().getItem().getDisplayName();
        }
    }
    
    @Override
    protected void create() {
        super.create();
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table t = createPlayerInventoryTable();
        supertable.add(t);
        Table crafting = new Table();
        List<RecipeUI> list = new List<>(CoreRes.SKIN.getSkin());
        Array<RecipeUI> a = new Array<>();
        for (SimpleRecipe sr : CraftingManager.instance().getRecipesSimple()) {
            RecipeUI rui = new RecipeUI();
            rui.simpleRecipe = sr;
            a.add(rui);
        }
        list.setItems(a);
        ClickListener listListener = new ClickListener() {
            private long last = 0;
            private RecipeUI lastS = null;
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                long current = System.currentTimeMillis();
                long dif = current - last;
                RecipeUI selected = list.getSelected();
                if (dif < InptMgr.DOUBLECLICK_DURATION_MS && lastS == selected && selected != null) {
                    if (lastS.simpleRecipe.matches(player.getInventory())) {
                        ItemStack is = lastS.simpleRecipe.craft(player.getInventory());
                        ItemStack leftover = InvUtil.insert(player.getInventory(), is);
                        if (!ItemStack.isEmptyOrNull(leftover)) {
                            //TODO drop leftovers
                        }
                    }
                } else {
                    last = current;
                    lastS = selected;
                }
            };
        };
        list.addListener(listListener);
        ScrollPane pane = new ScrollPane(list);
        stage.setScrollFocus(pane);
        crafting.add(pane).maxHeight(4 * Slot.SIZE + 2 * 0.5f + 10);
        supertable.add(crafting);
        stage.addActor(supertable);
    }
    
}
