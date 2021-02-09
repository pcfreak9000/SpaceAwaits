import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

import de.omnikryptec.math.Mathf;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.world.WorldAccessor;
import de.pcfreak9000.spaceawaits.world.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.MovingWorldEntityComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.RenderEntityComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.TextureSpriteAction;
import de.pcfreak9000.spaceawaits.world.gen.ChunkGenerator;
import de.pcfreak9000.spaceawaits.world.tile.Chunk;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class TestChunkGenerator implements ChunkGenerator {
    
    @Override
    public void generateChunk(Chunk chunk, WorldAccessor worldAccess) {
        for (int i = 0; i < Chunk.CHUNK_TILE_SIZE; i++) {
            for (int j = 0; j < Chunk.CHUNK_TILE_SIZE; j++) {
                if (!worldAccess.getMeta().inBounds(i + chunk.getGlobalTileX(), j + chunk.getGlobalTileY())) {
                    continue;
                }
                int value = 75
                        + Mathf.round(6 * Mathf.abs(MathUtils.sin(0.2f * (i + chunk.getGlobalTileX())))
                                + 20 * Mathf.abs(MathUtils.sin(0.05f * (i + chunk.getGlobalTileX()))));
                if (j + chunk.getGlobalTileY() > value) {
                    continue;
                }
                Tile t;
                if (j + chunk.getGlobalTileY() == 0) {
                    t = GameRegistry.TILE_REGISTRY.get("bottom");
                } else {
                    if (j + chunk.getGlobalTileY() == value) {
                        t = GameRegistry.TILE_REGISTRY.get("grass");
                    } else if (j + chunk.getGlobalTileY() >= value - 3) {
                        t = GameRegistry.TILE_REGISTRY.get("dirt");
                    } else {
                        t = GameRegistry.TILE_REGISTRY.get("stone");
                    }
                }
                
                if (t == DMod.instance.tstoneTile) {
                    if (Math.random() < 0.001) {
                        t = DMod.instance.laser;
                    }
                    if (Math.random() < 0.002) {
                        t = DMod.instance.torch;
                    }
                }
                chunk.setTile(t, i + chunk.getGlobalTileX(), j + chunk.getGlobalTileY());
                chunk.setTileBackground(t, i + chunk.getGlobalTileX(), j + chunk.getGlobalTileY());
            }
        }
        Entity entity = new Entity();
        entity.add(new MovingWorldEntityComponent());
        RenderEntityComponent rec = new RenderEntityComponent();
        Sprite s = new Sprite();
        s.setSize(200, 100);
        rec.sprite = s;
        rec.action = new TextureSpriteAction(DMod.instance.texture);
        entity.add(rec);
        TransformComponent tc = new TransformComponent();
        entity.add(tc);
        tc.position.set(chunk.getGlobalTileX() * Tile.TILE_SIZE,
                worldAccess.getMeta().getHeight() * Tile.TILE_SIZE-1);
        chunk.addEntity(entity);
        PhysicsComponent pc = new PhysicsComponent();
        pc.acceleration.set(0, -98.1f);
        pc.w = 200;
        pc.h = 100;
        entity.add(pc);
    }
}
