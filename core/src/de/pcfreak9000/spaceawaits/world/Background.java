package de.pcfreak9000.spaceawaits.world;

import de.pcfreak9000.spaceawaits.composer.ComposedTextureProvider;
import de.pcfreak9000.spaceawaits.world.ecs.DynamicAssetComponent;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.render.ecs.ParallaxComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderTextureComponent;

//@Deprecated
public class Background implements WorldEntityFactory {
    
    private final ComposedTextureProvider texture;
    
    private final float width;
    private final float height;
    
    public Background(ComposedTextureProvider texture, float width, float height) {
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
        DynamicAssetComponent dac = new DynamicAssetComponent();
        dac.dynamicAsset = texture;
        tex.texture = texture;
        tex.width = width;
        tex.height = height;
        e.add(dac);
        e.add(tc);
        e.add(pc);
        e.add(tex);
        e.add(new RenderComponent(-1, "entity"));
        return e;
    }
}
