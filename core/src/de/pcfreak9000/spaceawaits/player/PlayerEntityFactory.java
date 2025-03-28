package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.core.ecs.EntityFactory;
import de.pcfreak9000.spaceawaits.core.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.core.ecs.content.ActionComponent;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.chunk.ecs.ChunkTicketComponent;
import de.pcfreak9000.spaceawaits.world.ecs.OnSolidGroundComponent;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.ecs.RenderStatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.StatsComponent.StatData;
import de.pcfreak9000.spaceawaits.world.physics.SolidGroundContactListener;
import de.pcfreak9000.spaceawaits.world.physics.ecs.ContactListenerComponent;
import de.pcfreak9000.spaceawaits.world.physics.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.RenderLayers;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderRenderableComponent;

public class PlayerEntityFactory implements EntityFactory {

    public static final int ENTITY_FLAG_PLAYER = 2;

    public static Entity setupPlayerEntity(Player player) {
        Entity e = CoreRes.PLAYER_FACTORY.createEntity();
        e.getComponent(PlayerInputComponent.class).player = player;// Move this into dedicated component?
        return e;
    }

    @Override
    public Entity createEntity() {
        Entity e = new EntityImproved();
        e.flags = ENTITY_FLAG_PLAYER;
        OnSolidGroundComponent osgc = new OnSolidGroundComponent();
        SolidGroundContactListener l = new SolidGroundContactListener(osgc);
        PlayerInputComponent pic = new PlayerInputComponent();
        pic.maxXv = 45;
        pic.maxYv = 9.16f;
        e.add(pic);
        PhysicsComponent pc = new PhysicsComponent();
        RenderRenderableComponent rc = new RenderRenderableComponent();
        rc.width = 1.1f;
        rc.height = 1.9f;
        pic.offx = rc.width / 2f;
        pic.offy = rc.height / 2f;

        // rc.sprite = sprite;
        // rc.action = new TextureSpriteAction(CoreRes.HUMAN);
        rc.renderable = CoreRes.HUMAN;
        e.add(rc);
        TransformComponent tc = new TransformComponent();
        e.add(tc);
        e.add(pc);
        e.add(osgc);
        StatsComponent health = new StatsComponent();
        StatData healthStat = new StatsComponent.StatData(80, 100);
        health.put("health", healthStat);
        e.add(health);
        RenderStatsComponent rsc = new RenderStatsComponent();
        rsc.width = rc.width * 0.9f;
        rsc.xOff = rc.width * 0.05f;
        rsc.yOff = rc.height + 0.1f;
        e.add(rsc);
        pc.factory = new PlayerBodyFactory(rc.width, rc.height, l);
        // pc.factory = AABBBodyFactory.builder().dimensions(sprite.getWidth() * 0.7f,
        // sprite.getHeight() * 0.9f)
        // .offsets(sprite.getWidth() / 2, sprite.getHeight() / 2 * 0.9f).create();//new
        // AABBBodyFactory(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.9f,
        // sprite.getWidth() / 2, sprite.getHeight() / 2 * 0.9f);
        e.add(new SerializeEntityComponent(this));
        e.add(new RenderComponent(RenderLayers.ENTITY));
        e.add(new ContactListenerComponent(new PlayerContactListener()));
        ActionComponent actionComp = new ActionComponent();
        actionComp.actions.add(new TestExplodeTilesAction());
        actionComp.actions.add(new BreakAttackAction());
        actionComp.actions.add(new UseAction());
        e.add(actionComp);
        e.add(new ChunkTicketComponent(2));
        return e;
    }
}
