package de.pcfreak9000.spaceawaits.core;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.CoreEvents.UpdateResourcesEvent;

//This isn't ideal, especially for resource packs with other resolutions... Maybe use relative x,y,w,h instead? then this class becomes slightly better...
public class RegionedTextureProvider implements ITextureProvider {
    
    private ITextureProvider backing;
    private int x, y, w, h;
    
    private TextureRegion region;
    
    public RegionedTextureProvider(String texname, int x, int y, int w, int h) {
        this(TextureProvider.get(texname), x, y, w, h);
    }
    
    //"dynamic" providers like the animated one wont work. Use the String based constructor! (and make it work with textureatlases...)
    public RegionedTextureProvider(ITextureProvider backing, int x, int y, int w, int h) {
        SpaceAwaits.BUS.register(this);
        this.backing = backing;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    
    @Override
    public TextureRegion getRegion() {
        return region;
    }
    
    @EventSubscription(priority = -1)
    private void event2(UpdateResourcesEvent ev) {
        region = new TextureRegion(backing.getRegion(), x, y, w, h);
    }
}
