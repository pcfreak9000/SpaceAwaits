package de.pcfreak9000.spaceawaits.content.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent.StatData;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class ItemMedkitSimple extends Item {
    
    public ItemMedkitSimple() {
        setDisplayName("Simple Medkit");
        setTexture("medkit.png");
    }
    
    @Override
    public float getMaxRangeUse(Player player, ItemStack stackUsed) {
        return 5;
    }
    
    @Override
    public boolean onItemJustUse(Player player, ItemStack stackUsed, World world, float x, float y, int tilex,
            int tiley, TileLayer layer) {
        PhysicsSystem phys = world.getSystem(PhysicsSystem.class);
        Array<Object> ent = phys.queryXY(x, y, (udh, uc) -> udh.isEntity() && Components.STATS.has(udh.getEntity())
                && Components.STATS.get(udh.getEntity()).has("health"));
        if (ent.size > 0) {
            Entity entity = (Entity) ent.get(0);
            StatsComponent sc = Components.STATS.get(entity);
            StatData s = sc.get("health");
            if (!s.isMax()) {
                s.add(20);
                stackUsed.changeNumber(-1);
                return true;
            }
        }
        return false;
    }
}
