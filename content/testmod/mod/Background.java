package mod;

import de.pcfreak9000.spaceawaits.core.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.ParallaxComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.WorldGlobalComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.IRenderable;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderRenderableComponent;

public class Background implements WorldEntityFactory {
    
    private final IRenderable texture;
    
    private final float width;
    private final float height;
    
    private boolean frust;
    
    public float xoff, yoff, zdist;
    
    public float layer = -1000f;
    
    public Background(IRenderable texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.frust = true;
    }
    
    public Background(IRenderable texture) {
        this.texture = texture;
        this.frust = false;
        this.width = -1f;
        this.height = -1f;
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
        tex.dofrustumcheck = frust;
        pc.xOffset = xoff;
        pc.yOffset = yoff;
        pc.zdist = zdist;
        e.add(tc);
        e.add(pc);
        e.add(tex);
        e.add(new WorldGlobalComponent());
        e.add(new RenderComponent(layer));
        return e;
    }
}
