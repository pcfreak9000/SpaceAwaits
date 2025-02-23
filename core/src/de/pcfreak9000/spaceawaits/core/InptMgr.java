package de.pcfreak9000.spaceawaits.core;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.ObjectMap;

public class InptMgr {
    public static final long DOUBLECLICK_DURATION_MS = 300;
    
    public static final InptMgr WORLD = new InptMgr();
    public static final InptMgr UI = new InptMgr();
    
    private static ObjectMap<Object, ButtonKey[]> mappings = new ObjectMap<>();
    
    private static InputProcessor myProc = new InptProc();
    
    private boolean locked;
    
    private static float scrolledX, scrolledY;
    
    private static boolean[] justPressedKeys = new boolean[Keys.MAX_KEYCODE + 1];
    private static boolean[] justPressedButtons = new boolean[5];
    
    private static boolean[] justReleasedKeys = new boolean[Keys.MAX_KEYCODE + 1];
    private static boolean[] justReleasedButtons = new boolean[5];
    
    private static boolean justModeNormal = false;
    
    public static void init() {
        Gdx.input.setInputProcessor(myProc);
    }
    
    public static void register(Object id, int code, boolean isbutton) {
        register(id, new ButtonKey(code, isbutton));
    }
    
    //TODO could also register ids to specific InptMgrs, so the checking place doesn't need a "specific" InptMgr in mind
    //TODO could make precise buttonkey configs where other buttons must not be pressed
    
    public static void register(Object id, ButtonKey... buttons) {
        Objects.requireNonNull(buttons);
        if (buttons.length == 0) {
            throw new IllegalArgumentException("no buttons supplied");
        }
        mappings.put(id, buttons);
    }
    
    public boolean isPressed(Object id) {
        if (locked) {
            return false;
        }
        ButtonKey[] s = mappings.get(id);
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                if (s[i].isPressed()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isJustPressed(Object id) {
        if (locked) {
            return false;
        }
        ButtonKey[] s = mappings.get(id);
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                if (s[i].isJustPressed()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    //When locked this should somehow be remembered.... see next line
    //Opening some UI should maybe also somehow trigger this??
    //also see ActivatorSystem where this method is used
    public boolean isJustReleased(Object id) {
        if (locked) {
            return false;
        }
        ButtonKey[] s = mappings.get(id);
        if (s != null) {
            for (int i = 0; i < s.length; i++) {
                if (s[i].isJustReleased()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    //maybe make the scroll functions methods to return 0 if that InptMgr is locked
    
    public static float getScrollX() {
        return scrolledX;
    }
    
    public static float getScrollY() {
        return scrolledY;
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public void setLocked(boolean b) {
        locked = b;
        if (locked) {
            clear();
        }
    }
    
    public static void multiplex(InputProcessor other) {
        if (other != null) {
            InputMultiplexer m = new InputMultiplexer(other, myProc);
            Gdx.input.setInputProcessor(m);
        } else {
            Gdx.input.setInputProcessor(myProc);
        }
    }
    
    public static void setJustModeNormal(boolean b) {
        justModeNormal = b;
    }
    
    public static void clear() {
        scrolledX = 0;
        scrolledY = 0;
        for (int i = 0; i < justPressedKeys.length; i++) {
            justPressedKeys[i] = false;
        }
        for (int i = 0; i < justPressedButtons.length; i++) {
            justPressedButtons[i] = false;
        }
        for (int i = 0; i < justReleasedKeys.length; i++) {
            justReleasedKeys[i] = false;
        }
        for (int i = 0; i < justReleasedButtons.length; i++) {
            justReleasedButtons[i] = false;
        }
    }
    
    public static class ButtonKey {
        private final int code;
        private final boolean isButton;
        
        public ButtonKey(int code, boolean isbutton) {
            this.code = code;
            this.isButton = isbutton;
        }
        
        public boolean isPressed() {
            if (isButton) {
                return Gdx.input.isButtonPressed(code);
            } else {
                return Gdx.input.isKeyPressed(code);
            }
        }
        
        public boolean isJustPressed() {
            if (isButton) {
                return justModeNormal ? Gdx.input.isButtonJustPressed(code) : justPressedButtons[code];
            } else {
                return justModeNormal ? Gdx.input.isKeyJustPressed(code) : justPressedKeys[code];
            }
        }
        
        public boolean isJustReleased() {
            if (isButton) {
                return justReleasedButtons[code];
            } else {
                return justReleasedKeys[code];
            }
        }
    }
    
    private static class InptProc implements InputProcessor {
        
        @Override
        public boolean keyDown(int keycode) {
            justPressedKeys[keycode] = true;
            return false;
        }
        
        @Override
        public boolean keyUp(int keycode) {
            justReleasedKeys[keycode] = true;
            return false;
        }
        
        @Override
        public boolean keyTyped(char character) {
            return false;
        }
        
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            justPressedButtons[button] = true;
            return false;
        }
        
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            justReleasedButtons[button] = true;
            return false;
        }
        
        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }
        
        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }
        
        @Override
        public boolean scrolled(float amountX, float amountY) {
            scrolledX = amountX;
            scrolledY = amountY;
            return false;
        }
        
        @Override
        public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            return false;
        }
        
    }
}
