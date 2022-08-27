package de.pcfreak9000.spaceawaits.player;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.SolidGroundContactListener;
import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.RenderLayers;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.content.ActionComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.OnSolidGroundComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.RenderStatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.StatsComponent;
import de.pcfreak9000.spaceawaits.world.ecs.content.StatsComponent.StatData;
import de.pcfreak9000.spaceawaits.world.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.world.physics.ContactListenerComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderTextureComponent;

public class PlayerEntityFactory implements WorldEntityFactory {
    
    public static Entity setupPlayerEntity(Player player) {
        Entity e = CoreRes.PLAYER_FACTORY.createEntity();
        e.getComponent(PlayerInputComponent.class).player = player;//Move this into dedicated component?
        return e;
    }
    
    @Override
    public Entity createEntity() {
        Entity e = new EntityImproved();
        e.flags = 2;
        OnSolidGroundComponent osgc = new OnSolidGroundComponent();
        SolidGroundContactListener l = new SolidGroundContactListener(osgc);
        PlayerInputComponent pic = new PlayerInputComponent();
        pic.maxXv = 45;
        pic.maxYv = 110 / 12;
        e.add(pic);
        PhysicsComponent pc = new PhysicsComponent();
        RenderTextureComponent rc = new RenderTextureComponent();
        rc.width = 1.1f;
        rc.height = 2;
        pic.offx = rc.width / 2f;
        pic.offy = rc.height / 2f;
        
        //rc.sprite = sprite;
        //rc.action = new TextureSpriteAction(CoreRes.HUMAN);
        rc.texture = CoreRes.HUMAN;
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
        //        pc.factory = AABBBodyFactory.builder().dimensions(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.9f)
        //                .offsets(sprite.getWidth() / 2, sprite.getHeight() / 2 * 0.9f).create();//new AABBBodyFactory(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.9f, sprite.getWidth() / 2, sprite.getHeight() / 2 * 0.9f);
        e.add(new SerializeEntityComponent(this));
        e.add(new RenderComponent(RenderLayers.ENTITY));
        e.add(new ContactListenerComponent(new PlayerContactListener()));
        ActionComponent actionComp = new ActionComponent();
        actionComp.actions.add(new TestExplodeTilesAction());
        actionComp.actions.add(new BreakAttackAction());
        actionComp.actions.add(new UseAction());
        actionComp.actions.add(new ConsoleAction());
        e.add(actionComp);
        return e;
    }
}
