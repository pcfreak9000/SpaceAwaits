package de.pcfreak9000.spaceawaits.core;

import com.badlogic.ashley.core.Entity;

import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.HealthComponent;
import de.pcfreak9000.spaceawaits.world.ecs.OnSolidGroundComponent;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.physics.ContactListenerComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderTextureComponent;

public class PlayerEntityFactory implements WorldEntityFactory {
    
    public static Entity setupPlayerEntity(Player player) {
        Entity e = CoreRes.PLAYER_FACTORY.createEntity();
        e.getComponent(PlayerInputComponent.class).player = player;
        return e;
    }
    
    @Override
    public Entity createEntity() {
        Entity e = new EntityImproved();
        e.flags = 2;
        OnSolidGroundComponent osgc = new OnSolidGroundComponent();
        SolidGroundContactListener l = new SolidGroundContactListener(osgc);
        PlayerInputComponent pic = new PlayerInputComponent();
        pic.maxXv = 100 / 16;
        pic.maxYv = 100 / 16;
        e.add(pic);
        PhysicsComponent pc = new PhysicsComponent();
        RenderTextureComponent rc = new RenderTextureComponent();
        rc.width = 1.1f;
        rc.height = 3;
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
        HealthComponent health = new HealthComponent();
        health.maxHealth = 100;
        health.currentHealth = 100;
        e.add(health);
        pc.factory = new PlayerBodyFactory(rc.width, rc.height, l);
        //        pc.factory = AABBBodyFactory.builder().dimensions(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.9f)
        //                .offsets(sprite.getWidth() / 2, sprite.getHeight() / 2 * 0.9f).create();//new AABBBodyFactory(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.9f, sprite.getWidth() / 2, sprite.getHeight() / 2 * 0.9f);
        e.add(new SerializeEntityComponent(this));
        e.add(new RenderComponent(0.05f, "entity"));
        e.add(new ContactListenerComponent(new PlayerContactListener()));
        return e;
    }
}
