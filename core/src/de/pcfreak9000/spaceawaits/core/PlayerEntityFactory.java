package de.pcfreak9000.spaceawaits.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.serialize.SerializeEntityComponent;
import de.pcfreak9000.spaceawaits.world.WorldEntityFactory;
import de.pcfreak9000.spaceawaits.world.ecs.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.world.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.RenderEntityComponent;
import de.pcfreak9000.spaceawaits.world.ecs.entity.TextureSpriteAction;
import de.pcfreak9000.spaceawaits.world.physics.AABBBodyFactory;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsComponent;
import de.pcfreak9000.spaceawaits.world.tile.Tile;

public class PlayerEntityFactory implements WorldEntityFactory {
    @Override
    public Entity createEntity() {
        Entity e = new Entity();
        PlayerInputComponent pic = new PlayerInputComponent();
        pic.maxXv = 100;
        pic.maxYv = 100;
        e.add(pic);
        PhysicsComponent pc = new PhysicsComponent();
        Sprite sprite = new Sprite();
        sprite.setSize(Tile.TILE_SIZE * 2, Tile.TILE_SIZE * 3);
        RenderEntityComponent rc = new RenderEntityComponent();
        rc.sprite = sprite;
        rc.action = new TextureSpriteAction(CoreResources.HUMAN);
        e.add(rc);
        TransformComponent tc = new TransformComponent();
        tc.position.set(500, 2900);
        e.add(tc);
        e.add(pc);
        pc.factory = AABBBodyFactory.builder().dimensions(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.9f)
                .offsets(sprite.getWidth() / 2, sprite.getHeight() / 2 * 0.9f).create();//new AABBBodyFactory(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.9f, sprite.getWidth() / 2, sprite.getHeight() / 2 * 0.9f);
        e.add(new SerializeEntityComponent(this));
        return e;
    }
}
