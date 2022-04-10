package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

public class RenderItemStrategy implements IRenderStrategy {
    
    private static final Family FAMILY = Family.all(ItemStackComponent.class, TransformComponent.class).get();
    
    private static final Vector2[] offsets = new Vector2[8];
    
    static {
        for (int i = 0; i < offsets.length; i++) {
            float rx = (float) (Math.random() - 0.5);
            float ry = (float) (Math.random() - 0.5);
            offsets[i] = new Vector2(rx * Item.WORLD_SIZE * 0.8f, ry * Item.WORLD_SIZE * 0.8f);
        }
    }
    
    public RenderItemStrategy(GameRenderer renderer) {
        this.render = renderer;
        this.batch = this.render.getSpriteBatch();
    }
    
    private GameRenderer render;
    private SpriteBatch batch;
    
    @Override
    public void begin() {
        batch.begin();
    }
    
    @Override
    public void render(Entity e, float dt) {
        TransformComponent tc = Components.TRANSFORM.get(e);
        ItemStackComponent ic = Components.ITEM_STACK.get(e);
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
