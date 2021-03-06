package de.pcfreak9000.spaceawaits.core;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.CoreEvents.QueueResourcesEvent;
import de.pcfreak9000.spaceawaits.core.CoreEvents.UpdateResourcesEvent;

public class TextureProvider implements ITextureProvider {
    
    private static Map<String, TextureProvider> existingProvider = new HashMap<>();
    
    public static TextureProvider get(String name) {
        TextureProvider p = existingProvider.get(name);
        if (p == null) {
            p = new TextureProvider(name);
            existingProvider.put(name, p);
        }
        return p;
    }
    
    public static final ITextureProvider EMPTY = new ITextureProvider() {
        private final TextureProvider em = new TextureProvider();
        
        @Override
        public TextureRegion getRegion() {
            return em.getRegion();
        }
    };
    
    private String name;
    private TextureRegion region;
    private boolean registered;
    
    private TextureProvider() {
        SpaceAwaits.BUS.register(this);
    }
    
    private TextureProvider(String name) {
        this();
        setTexture(name);
    }
    
    public void setTexture(String name) {
        this.name = name;
    }
    
    @Override
    public TextureRegion getRegion() {
        return region;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    @EventSubscription
    private void event(QueueResourcesEvent ev) {
        if (name != null) {
            FileHandle filehandle = ev.assetMgr.getFileHandleResolver().resolve(name);
            if (filehandle.exists()) {
                ev.assetMgr.load(name, Texture.class);
                registered = true;
            } else {
                registered = false;
            }
        } else {
            registered = false;
        }
        this.region = null;
    }
    
    @EventSubscription
    private void event2(UpdateResourcesEvent ev) {
        Texture t = null;
        if (registered) {
            t = ev.assetMgr.get(name == null ? "missing_texture.png" : name, Texture.class);
        }
        if (t == null) {
            t = ev.assetMgr.get("missing_texture.png", Texture.class);
        }
        region = new TextureRegion(t);
    }
}
