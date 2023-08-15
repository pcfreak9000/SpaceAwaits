package mod;

import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.content.ParallaxComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.WorldGlobalComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.IRenderable;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderRenderableComponent;

public class Background implements WorldEntityFactory {
    
    private final IRenderable texture;
    
    private final float width;
    private final float height;
    
    public float xoff, yoff, w, h;
    
    public Background(IRenderable texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public EntityImproved createEntity() {
        EntityImproved e = new EntityImproved();
        ParallaxComponent pc = new ParallaxComponent();
        RenderRenderableComponent tex = new RenderRenderableComponent();
        TransformComponent tc = new TransformComponent();
        tex.renderable = texture;
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
