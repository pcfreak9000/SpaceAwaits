package de.pcfreak9000.spaceawaits.world.render;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.event.EventSubscription;
import de.omnikryptec.math.Mathf;
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
    
    private static final Vector2[] offsets = new Vector2[8];
    
    static {
        for (int i = 0; i < offsets.length; i++) {
            float rx = (float) (Math.random() - 0.5);
            float ry = (float) (Math.random() - 0.5);
            offsets[i] = new Vector2(rx * Item.WORLD_SIZE, ry * Item.WORLD_SIZE);
        }
    }
    
    public RenderItemStrategy() {
        SpaceAwaits.BUS.register(this);
    }
    
    private GameRenderer render;
    private SpriteBatch batch;
    
    @EventSubscription
    public void tileworldLoadingEvent(WorldEvents.SetWorldEvent svwe) {
        this.render = SpaceAwaits.getSpaceAwaits().getScreenManager().getGameRenderer();
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
            if (stack.getCount() == 1) {
                batch.draw(stack.getItem().getTextureProvider().getRegion(), tc.position.x, tc.position.y,
                        Item.WORLD_SIZE, Item.WORLD_SIZE);
            } else {
                float perc = stack.getCount() / (float) stack.getItem().getMaxStackSize();
                int amount = Mathf.ceili(offsets.length * perc);
                amount = Math.min(amount, stack.getCount());
                for (int i = 0; i < amount && i < offsets.length; i++) {
                    float x = offsets[i].x + tc.position.x;
                    float y = offsets[i].y + tc.position.y;
                    batch.draw(stack.getItem().getTextureProvider().getRegion(), x, y, Item.WORLD_SIZE,
                            Item.WORLD_SIZE);
                }
            }
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
