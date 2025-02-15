package layerteststuff;

import java.util.Random;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.content.entities.Entities;
import de.pcfreak9000.spaceawaits.content.items.Items;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.item.ItemStack;
import de.pcfreak9000.spaceawaits.world.WorldArea;
import de.pcfreak9000.spaceawaits.world.chunk.ITileArea;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.gen.feature.IFeature;

public class TreeFeature implements IFeature {
    
    @Override
    public boolean generate(WorldArea world, ITileArea tiles, int tx, int ty, Random rand) {
        Entity tree = Entities.TREE.createEntity();
        TransformComponent tc = Components.TRANSFORM.get(tree);
        tc.position.set(tx - 0.5f, ty + 1);
        world.spawnEntity(tree, false);
        if (rand.nextDouble() < 0.3) {
            ItemStack s = new ItemStack(Items.TWIG, rand.nextInt(1) + 1);
            s.dropRandomInTile(world, tx, ty + 1, rand);
        }
        return true;
    }
    
}
