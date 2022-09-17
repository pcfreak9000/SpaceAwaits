package de.pcfreak9000.spaceawaits.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;

public class GuiEsc extends GuiOverlay {
    
    @Override
    protected void create() {
        Table supertable = new Table();
        supertable.setFillParent(true);
        supertable.align(Align.center);
        Table table = new Table();
        TextButton buttonContinue = new TextButton("Continue", CoreRes.SKIN.getSkin());
        buttonContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                closeContainer();
            }
        });
        table.add(buttonContinue).pad(5);
        table.row();
        TextButton buttonSaveAll = new TextButton("Save All", CoreRes.SKIN.getSkin());
        buttonSaveAll.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                SpaceAwaits.getSpaceAwaits().getGameManager().getGameCurrent().saveGame();
            }
        });
        table.add(buttonSaveAll).pad(5);
        table.row();
        TextButton buttonSaveExit = new TextButton("Save and Exit", CoreRes.SKIN.getSkin());
        buttonSaveExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.queueSaveAndExitToMainMenu();
            }
        });
        table.add(buttonSaveExit).pad(5);
        table.row();
        ScrollPane pane = new ScrollPane(table, CoreRes.SKIN.getSkin());
        supertable.add(pane);
        stage.addActor(supertable);
    }
}
