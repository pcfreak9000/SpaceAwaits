package de.pcfreak9000.spaceawaits.content.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.module.ModuleBar;
import de.pcfreak9000.spaceawaits.module.ModuleUsage;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent.StatData;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class ItemRepairGun extends Item {
    private static final int MAX_USES = 2000;
    
    public ItemRepairGun() {
        setMaxStackSize(1);
        setDisplayName("Repair tool");
        setTexture("gun_0.png");
        setColor(Color.GOLD);
        addModule(ModuleUsage.ID, new ModuleUsage(MAX_USES));
        addModule(ModuleBar.ID, new ModuleBar());
    }
    
    @Override
    public float getMaxRangeUse(Player player, ItemStack stackUsed) {
        return 15;
    }
    
    @Override
    public boolean onItemUse(Player player, ItemStack stackUsed, World world, float x, float y, int tilex, int tiley,
            TileLayer layer) {
        PhysicsSystem phys = world.getSystem(PhysicsSystem.class);
        Array<Object> ent = phys.queryXY(x, y, (udh, uc) -> udh.isEntity() && Components.STATS.has(udh.getEntity())
                && Components.STATS.get(udh.getEntity()).has("mechHealth"));
        if (ent.size > 0) {
            Entity entity = (Entity) ent.get(0);
            StatsComponent sc = Components.STATS.get(entity);
            StatData s = sc.get("mechHealth");
            if (!s.isMax()) {
                s.add(5);
                ModuleUsage mus = stackUsed.getItem().getModule(ModuleUsage.ID);
                mus.use(stackUsed, true);
                return true;
            }
        }
        return false;
    }
}
