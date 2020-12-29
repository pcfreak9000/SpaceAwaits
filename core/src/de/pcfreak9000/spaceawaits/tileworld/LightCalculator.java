package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;

public class LightCalculator extends EntitySystem {
    /*
     * - Tiles in aktuellem Kameraausschnitt - boolean[][] mit Lichtquellen? - Licht
     * propagieren - Rendertexture aufbauen/rendern
     * 
     */
    private static final float LIGHT_SIZE = Tile.TILE_SIZE / 1;
    private static final int LIGHT_RADIUS_EXT = 20;
    
    private Pixmap lightMap;
    private World world;
    private WorldRenderInfo info;
    private Texture texture;
    
    public LightCalculator() {
        SpaceAwaits.BUS.register(this);
    }
    
    @EventSubscription
    public void event(WorldEvents.SetWorldEvent ev) {
        this.info = ev.worldMgr.getRenderInfo();
        this.world = ev.worldNew;
        Camera cam = ev.worldMgr.getRenderInfo().getCamera();
        resize(cam.viewportWidth, cam.viewportHeight);
    }
    
    public void resize(float widthf, float heightf) {
        if (this.lightMap != null) {
            this.lightMap.dispose();
            // this.texture.dispose();
        }
        int width = Mathf.floori(widthf / LIGHT_SIZE);
        int height = Mathf.floori(heightf / LIGHT_SIZE);
        this.lightMap = new Pixmap(width + LIGHT_RADIUS_EXT * 2, height + LIGHT_RADIUS_EXT * 2, Format.RGB888);
        //this.texture = new Texture(width + LIGHT_RADIUS_EXT * 2, height + LIGHT_RADIUS_EXT * 2, Format.RGB888);
    }
    
    @Override
    public void update(float deltaTime) {
        Camera cam = info.getCamera();
        int left = (Mathf.floori((cam.position.x - cam.viewportWidth / 2f) / LIGHT_SIZE) - LIGHT_RADIUS_EXT);
        int bottom = (Mathf.floori((cam.position.y - cam.viewportHeight / 2f) / LIGHT_SIZE) - LIGHT_RADIUS_EXT);
        this.lightMap.setColor(new Color(0.03f, 0.03f, 0.03f, 1));
        this.lightMap.fill();
        this.lightMap.setColor(Color.WHITE);
        this.lightMap.fillCircle(60 - left, 30 + bottom, 30);
        for (int x = 0; x < this.lightMap.getWidth(); x++) {
            for (int y = 0; y < this.lightMap.getHeight(); y++) {
                //Pixelmap coords sind anders als normale coords! (flipx bzw flipy benutzen vom spritebatch?)
            }
        }
        this.texture = new Texture(lightMap);
        this.texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        //this.texture.draw(lightMap, 0, 0);
        
        info.setMultiplicativeBlending();
        SpriteBatch batch = info.getSpriteBatch();
        batch.begin();
        batch.draw(texture, left*LIGHT_SIZE, bottom*LIGHT_SIZE, texture.getWidth() * LIGHT_SIZE, texture.getHeight() * LIGHT_SIZE);
        batch.end();
        info.setDefaultBlending();
        this.texture.dispose();
        this.texture = null;
    }
}
