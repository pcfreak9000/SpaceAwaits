package de.pcfreak9000.spaceawaits.tileworld;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class PointLight implements Light, Disposable {
    
    private Texture t;
    
    @Override
    public void drawLight(SpriteBatch batch, World world) {
        Pixmap p = new Pixmap(1, 1, Format.RGB888);
        p.setColor(Color.WHITE);
        p.fill();
        t = new Texture(p);
        p.dispose();
        batch.draw(t, 0, 0, 100, 100);
        throw new IllegalStateException();
        //can't directly dispose the texture here because the render call isnt out yet
    }
    
    @Override
    public void dispose() {
        t.dispose();
    }
}
