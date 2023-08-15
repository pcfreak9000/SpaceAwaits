package de.pcfreak9000.spaceawaits.world.ecs.content;

import com.badlogic.ashley.core.ComponentMapper;

import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkRenderComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.TickComponent;
import de.pcfreak9000.spaceawaits.world.physics.ContactListenerComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderBigTextureComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderFogComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderTextureComponent;
import de.pcfreak9000.spaceawaits.world.tile.ecs.BreakingTilesComponent;

public class Components {
    @Deprecated
    public static void registerComponents() {
        
        //What? Why? ????: GameRegistry.WORLD_COMPONENT_REGISTRY.register("spaceawaitsBreakable", BreakableComponent.class);
    }
    
    public static final ComponentMapper<RandomTickComponent> RANDOM_TICK = ComponentMapper
            .getFor(RandomTickComponent.class);
    
    public static final ComponentMapper<OnNeighbourChangeComponent> NEIGHGOUR_CHANGED = ComponentMapper
            .getFor(OnNeighbourChangeComponent.class);
    
    public static final ComponentMapper<BreakableComponent> BREAKABLE = ComponentMapper
            .getFor(BreakableComponent.class);
    
    public static final ComponentMapper<BreakingComponent> BREAKING = ComponentMapper.getFor(BreakingComponent.class);
    
    public static final ComponentMapper<TransformComponent> TRANSFORM = ComponentMapper
            .getFor(TransformComponent.class);
    
    public static final ComponentMapper<ActivatorComponent> ACTIVATOR = ComponentMapper
            .getFor(ActivatorComponent.class);
    
    public static final ComponentMapper<ActionComponent> ACTION = ComponentMapper.getFor(ActionComponent.class);
    
    /*** Mutually exclusive ***/
    
    public static final ComponentMapper<ChunkComponent> CHUNK = ComponentMapper.getFor(ChunkComponent.class);
    
    public static final ComponentMapper<WorldGlobalComponent> GLOBAL_MARKER = ComponentMapper
            .getFor(WorldGlobalComponent.class);
    
    /**************************/
    
    public static final ComponentMapper<PhysicsComponent> PHYSICS = ComponentMapper.getFor(PhysicsComponent.class);
    
    public static final ComponentMapper<FollowMouseComponent> FOLLOW_MOUSE = ComponentMapper
            .getFor(FollowMouseComponent.class);
    
    public static final ComponentMapper<PlayerInputComponent> PLAYER_INPUT = ComponentMapper
            .getFor(PlayerInputComponent.class);
    
    public static final ComponentMapper<OnSolidGroundComponent> ON_SOLID_GROUND = ComponentMapper
            .getFor(OnSolidGroundComponent.class);
    
    public static final ComponentMapper<StatsComponent> STATS = ComponentMapper.getFor(StatsComponent.class);//FIXMEx is this even used in core? well, yes, (just) for rendering, but makes sense as stats are pretty general
    
    public static final ComponentMapper<ItemStackComponent> ITEM_STACK = ComponentMapper
            .getFor(ItemStackComponent.class);
    
    public static final ComponentMapper<RenderTextureComponent> RENDER_TEXTURE = ComponentMapper
            .getFor(RenderTextureComponent.class);
    public static final ComponentMapper<RenderBigTextureComponent> RENDER_BIG_TEXTURE = ComponentMapper
            .getFor(RenderBigTextureComponent.class);
    
    public static final ComponentMapper<ParallaxComponent> PARALLAX = ComponentMapper.getFor(ParallaxComponent.class);
    
    public static final ComponentMapper<RenderFogComponent> RENDER_FOG = ComponentMapper
            .getFor(RenderFogComponent.class);
    
    public static final ComponentMapper<RenderComponent> RENDER = ComponentMapper.getFor(RenderComponent.class);
    
    public static final ComponentMapper<ChunkRenderComponent> RENDER_CHUNK = ComponentMapper
            .getFor(ChunkRenderComponent.class);
    
    public static final ComponentMapper<RenderStatsComponent> RENDER_STATS = ComponentMapper
            .getFor(RenderStatsComponent.class);
    
    public static final ComponentMapper<SerializeEntityComponent> SERIALIZE_ENTITY = ComponentMapper
            .getFor(SerializeEntityComponent.class);
    
    public static final ComponentMapper<TickComponent> TICK = ComponentMapper.getFor(TickComponent.class);
    
    public static final ComponentMapper<BreakingTilesComponent> TILES_BREAKING = ComponentMapper
            .getFor(BreakingTilesComponent.class);
    
    public static final ComponentMapper<DynamicAssetComponent> DYNAMIC_ASSET = ComponentMapper
            .getFor(DynamicAssetComponent.class);
    
    public static final ComponentMapper<ContactListenerComponent> CONTACT_LISTENER = ComponentMapper
            .getFor(ContactListenerComponent.class);
    
}
