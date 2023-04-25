package de.pcfreak9000.spaceawaits.content.gen;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.generation.IGeneratingLayer;
import de.pcfreak9000.spaceawaits.generation.HeightVariation;
import de.pcfreak9000.spaceawaits.item.loot.LootTable;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldBounds;
import de.pcfreak9000.spaceawaits.world.WorldUtil;
import de.pcfreak9000.spaceawaits.world.ecs.content.Components;
import de.pcfreak9000.spaceawaits.world.ecs.content.EntityInteractSystem;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.gen.IPlayerSpawn;
import de.pcfreak9000.spaceawaits.world.gen.IWorldGenerator;
import de.pcfreak9000.spaceawaits.world.gen.WorldPrimer;
import de.pcfreak9000.spaceawaits.world.gen.biome.Biome;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeChunkGenerator;
import de.pcfreak9000.spaceawaits.world.gen.biome.BiomeSystem;
import de.pcfreak9000.spaceawaits.world.gen.biome.GenFilter2DLayer;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import layerteststuff.TestBiome;
import mod.ComponentInventoryShip;
import mod.DMod;

public class SpaceSurfaceGenerator implements IGeneratingLayer<WorldPrimer, SpaceSurfaceParams> {
    
    private Vector2 spawn = null;
    
    @Override
    public WorldPrimer generate(SpaceSurfaceParams params) {
        //Aus dem SpaceSurface Typ den Biome GenFilter2D herausholen
        //Aus dem SpaceSurface Typ auch die möglichen Biome rausholen?
        //Oder aus dem SpaceSurface Typ gleich das/ein BiomeSystem rausholen? Oder gleich den ChunkGenerator? <-
        
        //BiomeSystem abstract mit Möglichkeiten zum Hinterlegen von Dingen wie terrainheight etc? eigene Map an Dingen, ansonsten auch über die Filter?
        //CaveSystem braucht BiomeSystem? Oder ist CaveSystem abstract und eine Subclass braucht BiomeSystem?
        
        //Form wird nicht vom Biome gemacht, sondern vom ChunkGenerator der Caves und Biomes etc beachtet, und wo kein Biome da auch kein Tile (leere Biome sind natürlich auch möglich)?
        
        Array<Biome> biomea = new Array<>(new Biome[] { new TestBiome(false), new TestBiome(true) });
        
        Random r = new RandomXS128(params.getSeed());
        //int higherlevelthick = Math.min(40, params.getHeight() / 3);
        int someint = params.getHeight() / 3;
        HeightVariation layer = new HeightVariation(someint, new LayerHeightVariation(params.getSeed() + 1, 8 + r.nextInt(5)));
        //LayerParams lowerLevel = new LayerParams(surface, 0, someint);
        // LayerParams higherLevel = new LayerParams(surface, someint, higherlevelthick);
        HeightComponent height = new HeightComponent(params.getSeed(),
                params.getHeight() / 3 + Math.min(40, params.getHeight() / 3), 30);//Not nice
        CaveSystem caves = new CaveSystem(params.getSeed());

        GenFilter2DLayer layerfilter = new GenFilter2DLayer(layer, null, null, "lower", "higher");
        GenFilter2DLayer airgroundfilter = new GenFilter2DLayer(height, layerfilter, null, null, new Object());
        
        BiomeSystem biomes = new BiomeSystem(airgroundfilter, biomea);
        biomes.setComponent(HeightComponent.class, height);
        biomes.setComponent(CaveSystem.class, caves);
        //setup SpaceSurface
        //SpaceSurface surface = new SpaceSurface(params);
        //setup worldprimer
        WorldPrimer p = new WorldPrimer();
        p.setWorldGenerator(new IWorldGenerator() {
            @Override
            public void generate(World world) {
                Entity ship = DMod.instance.fac.createEntity();
                TransformComponent tc = ship.getComponent(TransformComponent.class);
                Vector2 dim = ship.getComponent(PhysicsComponent.class).factory.boundingBoxWidthAndHeight();
                Vector2 s = WorldUtil.findSpawnpoint(world, dim.x, dim.y, 0, params.getHeight() / 3, params.getWidth(),
                        params.getHeight(), params.getSeed());
                spawn = s;
                tc.position.set(s);
                //WorldUtil.simImpact(world.getSystem(TileSystem.class), s.x + 2, s.y + 4, 10, 0, 0, 0);
                Components.STATS.get(ship).get("mechHealth").current = 1;
                LootTable.getFor("shipspawn").generate(new RandomXS128(params.getSeed()),
                        ship.getComponent(ComponentInventoryShip.class).invShip);
                //world.getSystem(EntityInteractSystem.class).spawnEntity(ship, false);
            }
            
            @Override
            public void onLoading(World world) {
                world.getSystem(EntityInteractSystem.class)
                        .spawnEntity(Registry.WORLD_ENTITY_REGISTRY.get("background.stars").createEntity(), false);
                world.getSystem(EntityInteractSystem.class)
                        .spawnEntity(Registry.WORLD_ENTITY_REGISTRY.get("background.planet").createEntity(), false);
                //world.spawnEntity(testFogEntity(), false);
            }
        });
        p.setPlayerSpawn(new IPlayerSpawn() {
            
            @Override
            public Rectangle getSpawnArea(Player player) {
                return new Rectangle(0, params.getHeight() / 3, params.getWidth(), params.getHeight());
            }
            
            @Override
            public Vector2 getPlayerSpawn(Player player, World world) {
                Vector2 dim = player.getPlayerEntity().getComponent(PhysicsComponent.class).factory
                        .boundingBoxWidthAndHeight();
                Rectangle rect = getSpawnArea(player);
                Vector2 s = WorldUtil.findSpawnpoint(world, dim.x, dim.y, 0, params.getHeight() / 3, params.getWidth(),
                        params.getHeight(), params.getSeed());
                return s;
                //return WorldUtil.findSpawnpoint(world, dim.x, dim.y, rect.x, rect.y, rect.width, rect.height);
            }
        });
        p.setWorldBounds(new WorldBounds(params.getWidth(), params.getHeight()));
        p.setChunkGenerator(new BiomeChunkGenerator(biomes, caves, params.getSeed()));
        return p;
    }
    
}
