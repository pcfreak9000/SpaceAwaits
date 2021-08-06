package de.pcfreak9000.spaceawaits.world;

import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.core.TextureProvider;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.render.TextureSpriteAction;
import de.pcfreak9000.spaceawaits.world.render.ecs.ParallaxComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
@Deprecated
public class Background implements WorldEntityFactory {
    
    private final TextureProvider texture;
    
    private final float width;
    private final float height;
    
    public Background(TextureProvider texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public EntityImproved createEntity() {
        EntityImproved e = new EntityImproved();
        ParallaxComponent pc = new ParallaxComponent();
        pc.sprite = new Sprite();
        pc.sprite.setSize(width, height);
        pc.action = new TextureSpriteAction(texture);
        e.add(pc);
        e.add(new RenderComponent(-1, "para"));
        return e;
    }
}
