package de.pcfreak9000.spaceawaits.core;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes;

public class SkinLoaderModified extends SkinLoader {

    public SkinLoaderModified(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    protected Skin newSkin(TextureAtlas atlas) {
        Skin s = super.newSkin(atlas);
        s.add("default", CoreRes.FONT, BitmapFont.class);
        return s;
    }

}
