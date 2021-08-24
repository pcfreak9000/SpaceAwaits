package de.pcfreak9000.spaceawaits.composer;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.pcfreak9000.spaceawaits.core.DynamicAsset;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;

public class ComposedTextureProvider implements ITextureProvider, DynamicAsset {
    
    private TextureRegion region;
    private Composer composer;
    
    public ComposedTextureProvider(Composer c) {
        this.composer = c;
    }
    
    @Override
    public TextureRegion getRegion() {
        return region;
    }
    
    @Override
    public void create() {
        this.region = new TextureRegion(composer.compose().getTexture());
    }
    
    @Override
    public void dispose() {
        this.region.getTexture().dispose();
    }
    
}
