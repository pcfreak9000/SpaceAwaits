package de.pcfreak9000.spaceawaits.composer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class ComposedImage implements Disposable {
    
    private final int width;
    private final int height;
    
    private final Texture texture;
    
    public ComposedImage(int width, int height, Texture texture) {
        this.width = width;
        this.height = height;
        this.texture = texture;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void render() {
        
    }
    
    @Deprecated
    public Texture getTexture() {
        return texture;
    }
    
    @Override
    public void dispose() {
        this.texture.dispose();
    }
    
}
