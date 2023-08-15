package de.pcfreak9000.spaceawaits.gui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes.EnumInputIds;

public class GuiChat extends GuiOverlay {
    
    private static final int MAX_HISTORY = 5000;
    
    private static List<String> history = new ArrayList<>();
    
    private TextField text;
    private int historyIndex = -1;
    private String current;
    
    @Override
    protected void create() {
        reactsToToggleInventory = false;
        Table t = new Table();
        t.setFillParent(true);
        t.bottom();
        text = new TextField("", CoreRes.SKIN.getSkin());
        t.add(text).fillX().expandX();
        stage.addActor(t);
        stage.setKeyboardFocus(text);
    }
    
    @Override
    public void actAndDraw(float dt) {
        if (InptMgr.isJustPressed(EnumInputIds.LastChatMsg)) {
            updateHistory(1);
        }
        if (InptMgr.isJustPressed(EnumInputIds.NextChatMsg)) {
            updateHistory(-1);
        }
        if (InptMgr.isJustPressed(EnumInputIds.SendMsg) && !justOpened()) {
            String input = text.getText();
            if (!input.isBlank()) {
                if (history.size() == 0 || !history.get(history.size() - 1).equals(input)) {
                    if (history.size() >= MAX_HISTORY) {
                        history.remove(0);
                    }
                    history.add(input);
                }
                if (input.startsWith("/")) {
                    input = input.substring(1);
                    gameScreen.getCommandContext().submitCommand(input);
                }
            }
            closeContainer();
        }
        super.actAndDraw(dt);
    }
    
    private void updateHistory(int i) {
        int old = historyIndex;
        historyIndex += i;
        historyIndex = Math.min(historyIndex, history.size() - 1);
        historyIndex = Math.max(historyIndex, -1);
        if (historyIndex != old) {
            if (historyIndex != -1) {
                if (old == -1) {
                    current = text.getText();
                }
                text.setText(history.get(history.size() - 1 - historyIndex));
                text.setCursorPosition(text.getText().length());
            } else {
                text.setText(current);
                text.setCursorPosition(text.getText().length());
            }
        }
    }
    
}
