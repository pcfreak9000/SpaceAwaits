package mod;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.StatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.StatsComponent.StatData;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class ItemRepairGun extends Item {
    
    public ItemRepairGun() {
        setMaxStackSize(1);
        setDisplayName("Repair tool");
        setTexture("gun_0.png");
        color().set(Color.GOLD);
    }
    
    @Override
    public boolean onItemUse(Player player, ItemStack stackUsed, World world, int tilex, int tiley, float x, float y,
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
                stackUsed.dealDamageUpdateBar(1, 2000, true);
                return true;
            }
        }
        return false;
    }
}
