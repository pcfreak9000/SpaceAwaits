package de.pcfreak9000.spaceawaits.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteCache;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.useOpenGL3(true, 3, 3);
        config.useVsync(!SpaceAwaits.DEBUG);
        config.setTitle(SpaceAwaits.NAME + " " + SpaceAwaits.VERSION);
        config.enableGLDebugOutput(SpaceAwaits.DEBUG, System.out);
        new Lwjgl3Application(new SpaceAwaits(), config);
    }
    
    private static class ReproduceBug extends ApplicationAdapter {
        
        private SpriteCache cache;
        private Texture texture;
        
        private static final int MAX_COUNT = 30;
        
        @Override
        public void create() {
            this.cache = new SpriteCache(1000, false);
            Pixmap p = new Pixmap(100, 100, Format.RGB888);
            p.setColor(1, 1, 1, 1);
            p.fill();
            p.setColor(Color.CORAL);
            p.fillCircle(50, 50, 50);
            this.texture = new Texture(p);
            p.dispose();
        }
        
        @Override
        public void render() {
            Gdx.gl.glClearColor(1, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            cache.clear();
            for (int i = 0; i < 5; i++) {
                cache.beginCache();
                for (int j = 0; j < MAX_COUNT; j++) {
                    cache.add(texture, i * 40, j * 10);
                }
                int id = cache.endCache();
                cache.begin();
                //***** Problem here: 
                cache.draw(id, 0, MAX_COUNT); //<- does only work for the first cache (offset and the internal cache.offset are both zero there)
                //cache.draw(id); //<- Works as expected
                //*****
                cache.end();
            }
        }
        
        @Override
        public void dispose() {
            this.cache.dispose();
            this.texture.dispose();
        }
        
    }
}
