package de.pcfreak9000.spaceawaits.menu;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.save.SaveMeta;

public class SelectSaveScreen extends MenuScreen {
    
    private static final long DOUBLECLICK_DURATION_MS = 300;
    
    private static final class SaveMetaUi {
        private final SaveMeta meta;
        
        public SaveMetaUi(SaveMeta m) {
            this.meta = m;
        }
        
        @Override
        public String toString() {
            return meta.getDisplayName();
        }
    }
    
    private Table table = new Table();
    private List<SaveMetaUi> savesList;
    
    public SelectSaveScreen(ScreenStateManager screenstatemgr, GuiScreenManager g) {
        super(g);
        savesList = new List<>(g.getSkin());
        TextButton newButton = new TextButton("New", g.getSkin());
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SpaceAwaits.getSpaceAwaits().getGameManager().createAndLoadGame(UUID.randomUUID().toString(),
                        new Random().nextLong());//TODO Use some other random instead
                screenstatemgr.setWorldScreen();
            }
        });
        
        TextButton playSelectedButton = new TextButton("Play selected", g.getSkin());
        playSelectedButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SaveMetaUi selected = savesList.getSelected();
                if (selected != null) {
                    SpaceAwaits.getSpaceAwaits().getGameManager().loadGame(selected.meta.getNameOnDisk());
                    screenstatemgr.setWorldScreen();
                }
            }
        });
        
        TextButton deleteSelectedButton = new TextButton("Delete selected", g.getSkin());//TODO confirmation dialog
        deleteSelectedButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SaveMetaUi selected = savesList.getSelected();
                if (selected != null) {
                    try {
                        SpaceAwaits.getSpaceAwaits().getGameManager().getSaveManager()
                                .deleteSave(selected.meta.getNameOnDisk());
                        updateSavesListEntries();
                    } catch (IOException e) {
                        e.printStackTrace();//Dialog instead
                    }
                }
            }
        });
        
        TextButton backButton = new TextButton("Back", g.getSkin());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenstatemgr.setMainMenuScreen();
            }
        });
        
        ClickListener listListener = new ClickListener() {
            private long last = 0;
            private SaveMetaUi lastS = null;
            
            @Override
            public void clicked(InputEvent event, float x, float y) {
                long current = System.currentTimeMillis();
                long dif = current - last;
                SaveMetaUi selected = savesList.getSelected();
                if (dif < DOUBLECLICK_DURATION_MS && lastS == selected && selected != null) {
                    SpaceAwaits.getSpaceAwaits().getGameManager().loadGame(selected.meta.getNameOnDisk());
                    screenstatemgr.setWorldScreen();
                    last = 0;
                } else {
                    last = current;
                    lastS = selected;
                }
            };
        };
        savesList.addListener(listListener);
        ScrollPane pane = new ScrollPane(savesList);
        table.setWidth(stage.getWidth());
        table.setHeight(stage.getHeight());
        table.align(Align.left);
        
        Table buttontable = new Table();
        buttontable.add(newButton).width(100).pad(5);
        buttontable.row();
        buttontable.add(playSelectedButton).width(100).pad(5);
        buttontable.row();
        buttontable.add(deleteSelectedButton).width(100).pad(5);
        buttontable.row();
        buttontable.add(backButton).width(100).pad(5);
        table.add(buttontable);
        table.add(pane);
        stage.addActor(table);
    }
    
    private void updateSavesListEntries() {
        Array<SaveMetaUi> array = new Array<>(SpaceAwaits.getSpaceAwaits().getGameManager().listSaves().stream()
                .map((sm) -> new SaveMetaUi(sm)).toArray(SaveMetaUi[]::new));
        savesList.setItems(array);
    }
    
    @Override
    public void show() {
        updateSavesListEntries();
        super.show();
    }
}
