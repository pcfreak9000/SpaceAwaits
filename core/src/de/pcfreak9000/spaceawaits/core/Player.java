package de.pcfreak9000.spaceawaits.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.item.Inventory;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.tileworld.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.tileworld.tile.Tile;

/**
 * Information about the player: level, ships, inventory, etc. Also the player
 * entity for surface worlds.
 *
 * @author pcfreak9000
 *
 */
public class Player {
    
    private final Entity playerEntity;
    
    private Inventory inventory;
    
    public Player() {
        this.playerEntity = createRawPlayerEntity();
        this.inventory = new Inventory();
    }
    
    private Entity createRawPlayerEntity() {
        Entity e = new Entity();
        PlayerInputComponent pic = new PlayerInputComponent();
        pic.maxXv = 100;
        pic.maxYv = 100;
        e.add(pic);
        PhysicsComponent pc = new PhysicsComponent();
        Sprite sprite = new Sprite();
        sprite.setSize(Tile.TILE_SIZE * 2, Tile.TILE_SIZE * 4);
        //FIXME resource reloading
        //sprite.getRenderData().setUVAndTexture(Omnikryptec.getTexturesS().get("mensch.png"));
     //   sprite.setLayer(100);
        //        SimpleSprite light = new SimpleSprite();
        //        light.setTexture(Omnikryptec.getTexturesS().get("light_2.png"));
        //        light.setWidth(Tile.TILE_SIZE * 80);
        //        light.setHeight(Tile.TILE_SIZE * 80);
        //        //light.setColor(new Color());
        //        //light.getColor().set(-100, 1, 1);
        //        light.getTransform().localspaceWrite().setTranslation(-light.getWidth() / 2 + sprite.getWidth() / 2,
        //                -light.getHeight() / 2 + sprite.getHeight() / 2);
        //        rc.light = light;
//        e.add(rc);
        TransformComponent tc = new TransformComponent();
        tc.position.set(500, 2900);
        e.add(tc);
        e.add(pc);
        pc.w = sprite.getWidth();
        pc.h = sprite.getHeight() * 0.95f;
        return e;
    }
    
    public Entity getPlayerEntity() {
        return this.playerEntity;
    }
    
    public Inventory getInventory() {
        return this.inventory;
    }
}
