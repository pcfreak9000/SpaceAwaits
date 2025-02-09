package de.pcfreak9000.spaceawaits.flat;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.player.InventoryPlayer;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;

public class FlatPlayer implements HudSupplier {
    private InventoryPlayer inventory;
    private Entity entity = new EntityImproved();

    public FlatPlayer() {
        this.inventory = new InventoryPlayer();
        entity.add(new TransformComponent());
        entity.add(new RenderComponent(0));
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public StatsComponent getStats() {
        return null;
    }

    @Override
    public InventoryPlayer getInventory() {
        return inventory;
    }

}
