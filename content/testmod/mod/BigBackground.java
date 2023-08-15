package mod;

import de.pcfreak9000.spaceawaits.composer.GeneratedTexture;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.content.DynamicAssetComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.ParallaxComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.WorldGlobalComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderBigTextureComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;

//@Deprecated
public class BigBackground implements WorldEntityFactory {
    
    private final GeneratedTexture texture;
    
    private final float width;
    private final float height;
    
    public float xoff, yoff, w, h;
    
    public BigBackground(GeneratedTexture texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public EntityImproved createEntity() {
        EntityImproved e = new EntityImproved();
        ParallaxComponent pc = new ParallaxComponent();
        RenderBigTextureComponent tex = new RenderBigTextureComponent();
        TransformComponent tc = new TransformComponent();
        DynamicAssetComponent dac = new DynamicAssetComponent();
        dac.dynamicAsset = texture;
        e.add(dac);
        tex.texture = texture;
        tex.width = width;
        tex.height = height;
        pc.xOffset = xoff;
        pc.yOffset = yoff;
        pc.widthScroll = w;
        pc.heightScroll = h;
        pc.width = tex.width;
        pc.height = tex.height;
        e.add(tc);
        e.add(pc);
        e.add(tex);
        e.add(new WorldGlobalComponent());
        e.add(new RenderComponent(-1000));
        return e;
    }
}
