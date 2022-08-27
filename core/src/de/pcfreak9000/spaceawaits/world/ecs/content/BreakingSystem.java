package de.pcfreak9000.spaceawaits.world.ecs.content;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemEntityFactory;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.World;

public class BreakingSystem extends IteratingSystem {
    
    private World world;
    
    public BreakingSystem(World world) {
        super(Family.all(BreakingComponent.class, BreakableComponent.class, TransformComponent.class).get());
        this.world = world;
    }
    
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BreakingComponent bc = Components.BREAKING.get(entity);
        if (bc.addProgress <= 0f) {
            entity.remove(BreakingComponent.class);
            return;
        }
        bc.progress += bc.addProgress * deltaTime;
        bc.addProgress = 0;
        if (bc.progress >= 1f) {
            entity.remove(BreakingComponent.class);
            BreakableComponent breakableComponent = Components.BREAKABLE.get(entity);
            boolean validated = breakableComponent.validate(entity);
            Array<ItemStack> drops = new Array<>();
            Random worldRandom = world.getWorldRandom();
            if (validated) {
                breakableComponent.breakable.onBreak(world, 0, 0, null, drops, worldRandom);
            }
            bc.breaker.onBreak(world, breakableComponent.breakable, 0, 0, null, drops, worldRandom);
            world.despawnEntity(entity);
            if (drops.size > 0) {
                TransformComponent tc = Components.TRANSFORM.get(entity);
                for (ItemStack s : drops) {
                    Entity e = ItemEntityFactory.setupItemEntity(s,
                            tc.position.x + worldRandom.nextFloat() / 2f - Item.WORLD_SIZE / 2,
                            tc.position.y + worldRandom.nextFloat() / 2f - Item.WORLD_SIZE / 2);
                    world.spawnEntity(e, false);
                }
                drops.clear();
            }
        }
    }
}
