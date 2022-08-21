package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.CoreRes;

public class ContainerInventoryPlayer extends GuiInventory {
    
    @Override
    protected void create() {
        super.create();
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table t = createPlayerInventoryTable();
        supertable.add(t);
        Table crafting = new Table();
        List<String> list = new List<>(CoreRes.SKIN.getSkin());
        Array<String> a = new Array<>();
        for (int i = 0; i < 100; i++) {
            a.add("tt" + i);
        }
        list.setItems(a);
        ScrollPane pane = new ScrollPane(list);
        pane.setSize(0.5f, 0.5f);
        crafting.add(pane);
        crafting.setClip(true);
        crafting.setSize(0.5f, 0.5f);
        supertable.setClip(true);
        supertable.add(crafting);
        stage.addActor(supertable);
    }
    
}
