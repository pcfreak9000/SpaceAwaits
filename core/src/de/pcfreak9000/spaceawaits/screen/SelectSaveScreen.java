package de.pcfreak9000.spaceawaits.screen;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.save.SaveMeta;

public class SelectSaveScreen extends MenuScreen {
    
    private static final class SaveMetaUi {
        private final SaveMeta meta;
        
        public SaveMetaUi(SaveMeta m) {
            this.meta = m;
        }
        
        @Override
        public String toString() {
            return meta.getDisplayName();
            //            StringBuilder b = new StringBuilder();
            //            b.append(meta.getDisplayName()).append('\n');
            //            b.append("Created: ").append(new Date(meta.getCreationTime()));
            //            return b.toString();
        }
    }
    
    private Table table = new Table();
    private List<SaveMetaUi> savesList;
    
    private static final TextFieldFilter NAME_FILTER = new TextFieldFilter() {
        
        @Override
        public boolean acceptChar(TextField textField, char c) {
            return Character.isAlphabetic(c) || Character.isDigit(c) || c == '-' || c == '_';
        }
    };
    
    public SelectSaveScreen(ScreenManager g, GuiHelper guiHelper) {
        super(guiHelper);
        savesList = new List<>(CoreRes.SKIN.getSkin());
        TextButton newButton = new TextButton("New Save", CoreRes.SKIN.getSkin());
        newButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextField nameField = new TextField("", CoreRes.SKIN.getSkin());
                nameField.setTextFieldFilter(NAME_FILTER);
                nameField.setMessageText("Name");
                TextField seedField = new TextField("", CoreRes.SKIN.getSkin());
                seedField.setMessageText("Seed");
                Dialog d = new Dialog("Create new world", CoreRes.SKIN.getSkin()) {
                    @Override
                    protected void result(Object object) {
                        if (object != null) {
                            String name = nameField.getText();
                            if (name.isBlank()) {
                                return;
                            }
                            String seeds = seedField.getText();
                            try {
                                SpaceAwaits.getSpaceAwaits().getGameManager().createAndLoadGame(name,
                                        getSeedFromInput(seeds));
                                g.setGameScreen();
                            } catch (Exception e) {
                                e.printStackTrace();
                                guiHelper.showDialog("Error", "An error occured: " + e.toString(), stage);
                            }
                        }
                    }
                };
                d.button("Create", new Object());
                d.button("Cancel");
                d.getContentTable().add(nameField);
                d.getContentTable().add(seedField);
                d.show(stage);
            }
        });
        
        TextButton playSelectedButton = new TextButton("Play selected", CoreRes.SKIN.getSkin());
        playSelectedButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SaveMetaUi selected = savesList.getSelected();
                if (selected != null) {
                    try {
                        SpaceAwaits.getSpaceAwaits().getGameManager().loadGame(selected.meta.getNameOnDisk(), false);
                        g.setGameScreen();
                    } catch (IOException e) {
                        e.printStackTrace();
                        guiHelper.showDialog("Error", "An error occured: " + e.toString(), stage);
                    }
                }
            }
        });
        
        TextButton deleteSelectedButton = new TextButton("Delete selected", CoreRes.SKIN.getSkin());
        deleteSelectedButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SaveMetaUi selected = savesList.getSelected();
                if (selected != null) {
                    Dialog del = new Dialog("Delete save", CoreRes.SKIN.getSkin()) {
                        @Override
                        protected void result(Object object) {
                            if (object != null) {
                                try {
                                    SpaceAwaits.getSpaceAwaits().getGameManager().getSaveManager()
                                            .deleteSave(selected.meta.getNameOnDisk());
                                    updateSavesListEntries();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    guiHelper.showDialog("Error",
                                            "An error occured while deleting the save:\n" + e.toString(), stage);
                                }
                            }
                        };
                    };
                    del.button("Delete", new Object());
                    del.button("Cancel");
                    del.show(stage);
                }
            }
        });
        
        TextButton backButton = new TextButton("Back", CoreRes.SKIN.getSkin());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                g.setMainMenuScreen();
            }
        });
        
        TextButton renameButton = new TextButton("Rename", CoreRes.SKIN.getSkin());
        renameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SaveMetaUi selected = savesList.getSelected();
                if (selected != null) {
                    TextField newName = new TextField(selected.meta.getDisplayName(), CoreRes.SKIN.getSkin());
                    newName.setTextFieldFilter(NAME_FILTER);
                    newName.setMessageText("New Name");
                    Dialog d = new Dialog("Rename", CoreRes.SKIN.getSkin()) {
                        @Override
                        protected void result(Object object) {
                            if (object != null) {
                                String s = newName.getText();
                                try {
                                    SpaceAwaits.getSpaceAwaits().getGameManager().getSaveManager()
                                            .rename(selected.meta.getNameOnDisk(), s);
                                    updateSavesListEntries();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    guiHelper.showDialog("Error",
                                            "An error occured while renaming the save:\n" + e.toString(), stage);
                                }
                            }
                        };
                    };
                    d.getContentTable().add(newName);
                    d.button("Rename", new Object());
                    d.button("Cancel");
                    d.show(stage);
                }
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
                if (dif < InptMgr.DOUBLECLICK_DURATION_MS && lastS == selected && selected != null) {
                    try {
                        SpaceAwaits.getSpaceAwaits().getGameManager().loadGame(selected.meta.getNameOnDisk(), false);
                        g.setGameScreen();
                        last = 0;
                    } catch (IOException e) {
                        e.printStackTrace();
                        guiHelper.showDialog("Error", "An error occured: " + e.toString(), stage);
                    }
                } else {
                    last = current;
                    lastS = selected;
                }
            };
        };
        savesList.addListener(listListener);
        ScrollPane pane = new ScrollPane(savesList);
        table.setFillParent(true);
        table.align(Align.left);
        Table buttontable = new Table();
        buttontable.add(newButton).width(100).pad(5);
        buttontable.row();
        buttontable.add(playSelectedButton).width(100).pad(5);
        buttontable.row();
        buttontable.add(renameButton).width(100).pad(5);
        buttontable.row();
        buttontable.add(deleteSelectedButton).width(100).pad(5);
        buttontable.row();
        buttontable.add(backButton).width(100).pad(5);
        table.add(buttontable);
        table.add(pane);
        stage.addActor(table);
    }
    
    private long getSeedFromInput(String in) {
        if (in.isEmpty()) {
            return new Random().nextLong();
        }
        try {
            long l = Long.parseLong(in);
            return l;
        } catch (NumberFormatException ex) {
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("MD5 not supported", nsae);
        }
        byte[] md5Bytes = md.digest(in.getBytes());
        ByteBuffer b = ByteBuffer.wrap(md5Bytes);
        return b.asLongBuffer().get(0);
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
