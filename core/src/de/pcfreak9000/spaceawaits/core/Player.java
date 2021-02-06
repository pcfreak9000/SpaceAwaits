package de.pcfreak9000.spaceawaits.core;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;

import de.pcfreak9000.spaceawaits.item.Inventory;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PhysicsComponent;
import de.pcfreak9000.spaceawaits.tileworld.ecs.PlayerInputComponent;
import de.pcfreak9000.spaceawaits.tileworld.ecs.TransformComponent;
import de.pcfreak9000.spaceawaits.tileworld.ecs.entity.RenderEntityComponent;
import de.pcfreak9000.spaceawaits.tileworld.ecs.entity.TextureSpriteAction;
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
        sprite.setSize(Tile.TILE_SIZE * 2, Tile.TILE_SIZE * 3);
        RenderEntityComponent rc = new RenderEntityComponent();
        rc.sprite = sprite;
        rc.action = new TextureSpriteAction(SpaceAwaits.getSpaceAwaits().mensch);
        e.add(rc);
        //FIXME resource reloading
        TransformComponent tc = new TransformComponent();
        tc.position.set(500, 2900);
        e.add(tc);
        e.add(pc);
        pc.w = sprite.getWidth() * 0.95f;
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
