package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.SpaceAwaits;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.ecs.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;

public class RenderItemStrategy implements IRenderStrategy {
    
    private static final Family FAMILY = Family.all(ItemStackComponent.class, TransformComponent.class).get();
    
    private final ComponentMapper<TransformComponent> transformMapper = ComponentMapper
            .getFor(TransformComponent.class);
    private final ComponentMapper<ItemStackComponent> renderMapper = ComponentMapper.getFor(ItemStackComponent.class);
    
    public RenderItemStrategy() {
        SpaceAwaits.BUS.register(this);
    }
    
    private WorldRenderer render;
    private SpriteBatch batch;
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.render = SpaceAwaits.getSpaceAwaits().getScreenManager().getWorldRenderer();
        this.batch = render.getSpriteBatch();
    }
    
    @Override
    public void begin() {
        batch.begin();
    }
    
    @Override
    public void render(Entity e, float dt) {
        TransformComponent tc = transformMapper.get(e);
        ItemStackComponent ic = renderMapper.get(e);
        ItemStack stack = ic.stack;
        if (stack != null && !stack.isEmpty()) {
            batch.setColor(stack.getItem().color());
            //TODO indicate count by more drawcalls?
            batch.draw(stack.getItem().getTextureProvider().getRegion(), tc.position.x, tc.position.y, Item.WORLD_SIZE,
                    Item.WORLD_SIZE);
        }
    }
    
    @Override
    public void end() {
        batch.end();
    }
    
    @Override
    public Family getFamily() {
        return FAMILY;
    }
    
}
