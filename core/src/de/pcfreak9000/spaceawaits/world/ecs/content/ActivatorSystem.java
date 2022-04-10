package de.pcfreak9000.spaceawaits.world.ecs.content;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.player.Player;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.WorldEvents;
import de.pcfreak9000.spaceawaits.world.ecs.SystemCache;
import de.pcfreak9000.spaceawaits.world.physics.PhysicsSystem;
import de.pcfreak9000.spaceawaits.world.physics.UserDataHelper;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;

public class ActivatorSystem extends EntitySystem {
    
    private static final Family FAMILY = Family.all(ActionComponent.class).get();
    
    private static final SystemCache<PhysicsSystem> phys = new SystemCache<>(PhysicsSystem.class);
    
    private static final Comparator<Entity> COMP = (e0, e1) -> {
        return (int) Math.signum(Components.ACTIVATOR.get(e1).layer - Components.ACTIVATOR.get(e0).layer);
    };
    
    private GameRenderer gameRend;
    private World world;
    private Player player;
    private ImmutableArray<Entity> entities;
    
    private final UserDataHelper udh = new UserDataHelper();
    private final Set<Entity> entitySet = new HashSet<>();
    private final List<Entity> entityList = new ArrayList<>();
    
    public ActivatorSystem(GameRenderer rend, World world) {
        this.gameRend = rend;
        this.world = world;
        this.world.getWorldBus().register(this);
    }
    
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
        if (gameRend.isGuiContainerOpen()) {
            return;
        }
        entitySet.clear();
        entityList.clear();
        Vector2 mouse = gameRend.getMouseWorldPos();
        phys.get(getEngine()).queryAABB((fix, uc) -> {
            if (fix.testPoint(uc.in(mouse.x), uc.in(mouse.y))) {//really test point? or let the activators decide if they like something?
                udh.set(fix.getUserData(), fix);
                if (udh.isEntity()) {
                    Entity e = udh.getEntity();
                    if (Components.ACTIVATOR.has(e)) {
                        if (entitySet.add(e)) {
                            entityList.add(e);
                        }
                    }
                }
            }
            return true;
        }, mouse.x - 0.01f, mouse.y - 0.01f, mouse.x + 0.01f, mouse.y + 0.01f);
        for (Entity e : entities) {
            ActionComponent ac = Components.ACTION.get(e);
            for (Action a : ac.actions) {
                if (InptMgr.isPressed(a.getInputKey())) {
                    if (a.handle(mouse.x, mouse.y, world, e)) {
                        return;
                    }
                }
            }
        }
        entityList.sort(COMP);
        for (Entity e : entityList) {
            ActivatorComponent ac = Components.ACTIVATOR.get(e);
            for (Activator a : ac.activators) {
                if (InptMgr.isPressed(a.getInputKey())) {
                    if (a.handle(mouse.x, mouse.y, e, this.world, this.player.getPlayerEntity())) {
                        return;
                    }
                }
            }
        }
        
    }
    
}
