package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.ItemStackComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.render.GameScreen;

public class RenderItemStrategy implements IRenderStrategy {
    
    private static final Family FAMILY = Family.all(ItemStackComponent.class, TransformComponent.class).get();
    
    private static final Vector2[] offsets = new Vector2[8];
    
    private static final float HIGHLIGHTER_BASEOFFSET = 0.15f;
    private static final Color HIGHLIGHTER_COLOR = new Color(1, 1, 1, 0.9f);
    
    static {
        for (int i = 0; i < offsets.length; i++) {
            float rx = (float) (Math.random() - 0.5);
            float ry = (float) (Math.random() - 0.5);
            offsets[i] = new Vector2(rx * Item.WORLD_SIZE * 0.8f, ry * Item.WORLD_SIZE * 0.8f);
        }
        
    }
    
    public RenderItemStrategy(GameScreen renderer) {
        this.render = renderer;
        this.batch = this.render.getSpriteBatch();
        this.cam = renderer.getCamera();
    }
    
    private GameScreen render;
    private SpriteBatch batch;
    
    private Camera cam;
    private float mod = 0;
    
    @Override
    public void begin() {
        mod = MathUtils.sin(render.getRenderTime() * 3) * 0.045f + 0.0225f;//This might still be called multiple times per frame...
        batch.begin();
    }
    
    @Override
    public void render(Entity e, float dt) {
        TransformComponent tc = Components.TRANSFORM.get(e);
        if (!cam.frustum.boundsInFrustum(tc.position.x - Item.WORLD_SIZE * 0.5f, tc.position.y - Item.WORLD_SIZE * 0.5f,
                0, Item.WORLD_SIZE, Item.WORLD_SIZE, 0)) {
            return;
        } //TODO move frustum checks somewhere else and reuse code?
        ItemStackComponent ic = Components.ITEM_STACK.get(e);
        ItemStack stack = ic.stack;
        if (stack != null && !stack.isEmpty()) {
            if (stack.getCount() == 1) {
                batch.setColor(stack.getItem().getColor());
                batch.draw(stack.getItem().getTextureProvider().getRegion(), tc.position.x - mod, tc.position.y - mod,
                        Item.WORLD_SIZE + mod * 2, Item.WORLD_SIZE + mod * 2);
                batch.setColor(HIGHLIGHTER_COLOR);
                batch.draw(CoreRes.ITEM_HIGHLIGHT.getRegion(), tc.position.x - HIGHLIGHTER_BASEOFFSET - mod,
                        tc.position.y - HIGHLIGHTER_BASEOFFSET - mod,
                        Item.WORLD_SIZE + HIGHLIGHTER_BASEOFFSET * 2 + mod * 2,
                        Item.WORLD_SIZE + HIGHLIGHTER_BASEOFFSET * 2 + mod * 2);
            } else {
                float perc = stack.getCount() / (float) stack.getItem().getMaxStackSize();
                int amount = Mathf.ceili(offsets.length * perc);
                amount = Math.min(amount, stack.getCount());
                for (int i = 0; i < amount && i < offsets.length; i++) {
                    float x = offsets[i].x + tc.position.x;
                    float y = offsets[i].y + tc.position.y;
                    batch.setColor(stack.getItem().getColor());
                    batch.draw(stack.getItem().getTextureProvider().getRegion(), x - mod, y - mod,
                            Item.WORLD_SIZE + mod * 2, Item.WORLD_SIZE + mod * 2);
                    batch.setColor(HIGHLIGHTER_COLOR);
                    batch.draw(CoreRes.ITEM_HIGHLIGHT.getRegion(), x - HIGHLIGHTER_BASEOFFSET - mod,
                            y - HIGHLIGHTER_BASEOFFSET - mod, Item.WORLD_SIZE + HIGHLIGHTER_BASEOFFSET * 2 + mod * 2,
                            Item.WORLD_SIZE + HIGHLIGHTER_BASEOFFSET * 2 + mod * 2);
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
