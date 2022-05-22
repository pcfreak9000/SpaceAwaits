package mod;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.item.Item;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;

public class ItemRepairGun extends Item {
    
    public ItemRepairGun() {
        setMaxStackSize(1);
        setDisplayName("Repair tool");
        setTexture("gun_0.png");
        color().set(Color.GOLD);
    }
    
    private static final ComponentMapper<DamagedComponent> MAP = ComponentMapper.getFor(DamagedComponent.class);//Move
    
    @Override
    public boolean onItemUse(Player player, ItemStack stackUsed, World world, int tilex, int tiley, float x, float y,
            TileLayer layer) {
        PhysicsSystem phys = world.getSystem(PhysicsSystem.class);
        Array<Object> ent = phys.queryXY((udh, uc) -> udh.isEntity() && MAP.has(udh.getEntity()), x, y);
        if (ent.size > 0) {
            Entity entity = (Entity) ent.get(0);
            DamagedComponent dc = MAP.get(entity);
            dc.damage -= 0.0075;
            stackUsed.dealDamageBarIndic(1, 2000, true);
            if (dc.damage <= 0) {
                entity.remove(DamagedComponent.class);
            }
            return true;
        }
        return false;
    }
}
