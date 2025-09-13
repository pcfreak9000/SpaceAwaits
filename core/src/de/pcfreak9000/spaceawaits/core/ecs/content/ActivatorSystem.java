package de.pcfreak9000.spaceawaits.core.ecs.content;

import java.util.Comparator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.core.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;

public abstract class ActivatorSystem extends EntitySystem {
    
    private static final Family FAMILY = Family.all(ActionComponent.class).get();
    
    private static final Comparator<Object> COMP = (e0, e1) -> {
        return (int) Math
                .signum(Components.ACTIVATOR.get((Entity) e1).layer - Components.ACTIVATOR.get((Entity) e0).layer);
    };
    
    private final SystemCache<CameraSystem> camsys = new SystemCache<>(CameraSystem.class);
    @Deprecated
    private Player player;
    private ImmutableArray<Entity> entities;
    
    public ActivatorSystem() {
    }
    
    protected abstract Array<Object> getEntities(float mousex, float mousey);
    
    @EventSubscription
    private void plEv(WorldEvents.PlayerJoinedEvent ev) {
        this.player = ev.player;
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        this.entities = engine.getEntitiesFor(FAMILY);
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        this.entities = null;
    }
    
    @Override
    public void update(float deltaTime) {
        if (InptMgr.WORLD.isLocked()) {
            return;
        }
        Vector2 mouse = camsys.get(getEngine()).getMouseWorldPos();
        if (!camsys.get(getEngine()).getCamera().frustum.pointInFrustum(mouse.x, mouse.y, 0)) {
            return;
        }
        Array<Object> ents = getEntities(mouse.x, mouse.y);
        for (Entity e : entities) {
            ActionComponent ac = Components.ACTION.get(e);
            for (Action a : ac.actions) {
                if (a.isContinuous() ? InptMgr.WORLD.isPressed(a.getInputKey())
                        : InptMgr.WORLD.isJustPressed(a.getInputKey())) {
                    if (a.handle(mouse.x, mouse.y, getEngine(), e)) {
                        return;
                    }
                } else if (InptMgr.WORLD.isJustReleased(a.getInputKey())) {//Opening some UI should maybe also somehow trigger this??
                    if (a.handleRelease(mouse.x, mouse.y, getEngine(), e)) {
                        return;//Move this up to the top? What if the entity is removed from the system in the meantime? 
                        //Or leave this just here? should work fine
                    }
                }
            }
        }
        //this could theoretically go into an Action as well (ActivationAction or something), but this way, the activated entity can have any key mapping to that activation 
        //and isn't bound by the single key(combination) of the Action
        ents.sort(COMP);
        for (Object o : ents) {
            Entity e = (Entity) o;
            ActivatorComponent ac = Components.ACTIVATOR.get(e);
            for (Activator a : ac.activators) {
                if (a.isContinuous() ? InptMgr.WORLD.isPressed(a.getInputKey())
                        : InptMgr.WORLD.isJustPressed(a.getInputKey())) {
                    if (a.handle(mouse.x, mouse.y, e, getEngine(), this.player.getTileWorldPlayer().getPlayerEntity())) {//TODO change to the entity which is inputting stuff
                        return;//this.player.getPlayerEntity()
                    }
                }
            }
        }
        
    }
    
}
