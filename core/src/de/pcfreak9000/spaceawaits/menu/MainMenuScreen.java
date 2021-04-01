package de.pcfreak9000.spaceawaits.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;

public class MainMenuScreen extends MenuScreen {
    
    private Table table = new Table();
    
    public MainMenuScreen(ScreenStateManager screenstatemgr, GuiScreenManager g) {
        super(g);
        TextButton playButton = new TextButton("Play", g.getSkin());
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenstatemgr.setSelectSaveScreen();
            }
        });
        TextButton exitButton = new TextButton("Quit", g.getSkin());
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SpaceAwaits.getSpaceAwaits().exit();
            }
        });
        Label label = new Label(SpaceAwaits.NAME, g.getSkin());
        label.setFontScale(2);
        table.setWidth(this.stage.getWidth());
        table.setHeight(this.stage.getHeight());
        table.align(Align.center);
        table.add(label);
        table.row();
        table.add(playButton).width(110).padBottom(5);
        table.row();
        table.add(exitButton).width(110);
        stage.addActor(table);
    }
    
}
