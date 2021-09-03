

import de.pcfreak9000.spaceawaits.core.DynamicAsset;
import de.pcfreak9000.spaceawaits.core.ITextureProvider;
import de.pcfreak9000.spaceawaits.world.ecs.DynamicAssetComponent;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.ParallaxComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderTextureComponent;

//@Deprecated
public class Background implements WorldEntityFactory {
    
    private final ITextureProvider texture;
    
    private final float width;
    private final float height;
    
    public float xoff, yoff,w,h;
    
    public Background(ITextureProvider texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public EntityImproved createEntity() {
        EntityImproved e = new EntityImproved();
        ParallaxComponent pc = new ParallaxComponent();
        RenderTextureComponent tex = new RenderTextureComponent();
        TransformComponent tc = new TransformComponent();
        if (texture instanceof DynamicAsset) {
            DynamicAssetComponent dac = new DynamicAssetComponent();
            dac.dynamicAsset = (DynamicAsset) texture;
            e.add(dac);
        }
        tex.texture = texture;
        tex.width = width;
        tex.height = height;
        pc.xOffset = xoff;
        pc.yOffset = yoff;
        pc.widthScroll = w;
        pc.heightScroll = h;
        e.add(tc);
        e.add(pc);
        e.add(tex);
        e.add(new RenderComponent(-1000, "entity"));
        return e;
    }
}
