package de.pcfreak9000.spaceawaits.world;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.core.InptMgr;
import de.pcfreak9000.spaceawaits.world.ecs.RenderSystemMarker;

public class ModifiedEngine extends Engine {
    
    private Object compOpHandler;
    private Method compOpProcessMethod;
    
    private Field updatingField;
    
    private Object entityManager;
    private Method entMgrProcessPendOpMethod;
    
    private float timeAccum;
    private final float stepsize;
    
    private final Array<EntitySystem> rendersystems = new Array<>();
    private final Array<EntitySystem> logicsystems = new Array<>();
    
    public ModifiedEngine(float stepsize) {
        setupReflectionStuff();
        this.stepsize = stepsize;
    }
    
    @Override
    public void addSystem(EntitySystem system) {
        EntitySystem old = getSystem(system.getClass());
        super.addSystem(system);
        Array<EntitySystem> container = system instanceof RenderSystemMarker ? this.rendersystems : this.logicsystems;
        if (old != null) {
            container.removeValue(old, true);
        }
        container.add(system);
    }
    
    @Override
    public void removeSystem(EntitySystem system) {
        super.removeSystem(system);
        Array<EntitySystem> container = system instanceof RenderSystemMarker ? this.rendersystems : this.logicsystems;
        container.removeValue(system, true);
    }
    
    @Override
    public void update(float deltaTime) {
        if (isUpdating()) {
            throw new IllegalStateException("Cannot call update() on an Engine that is already updating.");
        }
        timeAccum += deltaTime;
        if (timeAccum > 0.067f) {
            timeAccum = 0.067f;
        }
        setUpdating(true);
        try {
            while (timeAccum >= stepsize) {
                timeAccum -= stepsize;
                updateCycleFor(stepsize, logicsystems);
                InptMgr.clear();//Hmmm. Input is kinda cursed right now?
            }
            updateCycleFor(deltaTime, rendersystems);
        } finally {
            setUpdating(false);
        }
    }
    
    private void updateCycleFor(float dt, Array<EntitySystem> systems) {
        for (int i = 0; i < systems.size; i++) {
            EntitySystem rsys = systems.get(i);
            
            if (rsys.checkProcessing()) {
                rsys.update(dt);
            }
            
            compOperationsProcessOperations();
            entMgrProcessPendingOperations();
        }
    }
    
    private void setupReflectionStuff() {
        try {
            Class<Engine> cl = Engine.class;
            Field opPoolField = cl.getDeclaredField("componentOperationHandler");
            opPoolField.setAccessible(true);
            compOpHandler = opPoolField.get(this);
            compOpProcessMethod = compOpHandler.getClass().getMethod("processOperations");
            compOpProcessMethod.setAccessible(true);
            
            updatingField = cl.getDeclaredField("updating");
            updatingField.setAccessible(true);
            
            Field entMgrField = cl.getDeclaredField("entityManager");
            entMgrField.setAccessible(true);
            entityManager = entMgrField.get(this);
            entMgrProcessPendOpMethod = entityManager.getClass().getMethod("processPendingOperations");
            entMgrProcessPendOpMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setUpdating(boolean b) {
        try {
            updatingField.setBoolean(this, b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean isUpdating() {
        try {
            return updatingField.getBoolean(this);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void entMgrProcessPendingOperations() {
        try {
            entMgrProcessPendOpMethod.invoke(entityManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void compOperationsProcessOperations() {
        try {
            compOpProcessMethod.invoke(compOpHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
