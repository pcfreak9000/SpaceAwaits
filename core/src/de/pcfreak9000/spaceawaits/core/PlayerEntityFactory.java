package de.pcfreak9000.spaceawaits.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.RenderEntityComponent;
import de.pcfreak9000.spaceawaits.world.physics.ContactListenerComponent;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.render.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.TextureSpriteAction;

public class PlayerEntityFactory implements WorldEntityFactory {
    @Override
    public Entity createEntity() {
        Entity e = new EntityImproved();
        e.flags = 2;
        SolidGroundContactListener l = new SolidGroundContactListener();
        PlayerInputComponent pic = new PlayerInputComponent();
        pic.solidGround = l;
        pic.maxXv = 100 / 16;
        pic.maxYv = 100 / 16;
        e.add(pic);
        PhysicsComponent pc = new PhysicsComponent();
        Sprite sprite = new Sprite();
        sprite.setSize(2, 3);
        pic.offx = sprite.getWidth() / 2f;
        pic.offy = sprite.getHeight() / 2f;
        RenderEntityComponent rc = new RenderEntityComponent();
        rc.sprite = sprite;
        rc.action = new TextureSpriteAction(CoreRes.HUMAN);
        e.add(rc);
        TransformComponent tc = new TransformComponent();
        e.add(tc);
        e.add(pc);
        pc.factory = new PlayerBodyFactory(sprite.getWidth(), sprite.getHeight(), l);
        //        pc.factory = AABBBodyFactory.builder().dimensions(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.9f)
        //                .offsets(sprite.getWidth() / 2, sprite.getHeight() / 2 * 0.9f).create();//new AABBBodyFactory(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.9f, sprite.getWidth() / 2, sprite.getHeight() / 2 * 0.9f);
        e.add(new SerializeEntityComponent(this));
        e.add(new RenderComponent(1, "entity"));
        e.add(new ContactListenerComponent(new PlayerContactListener()));
        return e;
    }
}
