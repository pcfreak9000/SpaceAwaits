package de.pcfreak9000.spaceawaits.screen;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes;

public class MainMenuScreen extends MenuScreen {
    
    private Table table = new Table();
    
    public MainMenuScreen(ScreenManager g, GuiHelper guiHelper) {
        super(guiHelper);
        TextButton playButton = new TextButton("Play", CoreRes.SKIN.getSkin());
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                g.setSelectSaveScreen();
            }
        });
        TextButton exitButton = new TextButton("Quit", CoreRes.SKIN.getSkin());
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SpaceAwaits.getSpaceAwaits().exit();
            }
        });
        Label label = new Label(SpaceAwaits.NAME, CoreRes.SKIN.getSkin());
        label.setFontScale(4);
        table.setFillParent(true);
        table.align(Align.center);
        table.add(label);
        table.row();
        table.add(playButton).width(110).padBottom(5);
        table.row();
        table.add(exitButton).width(110);
        stage.addActor(table);
    }
    
}
