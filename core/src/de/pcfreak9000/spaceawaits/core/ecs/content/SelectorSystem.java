package de.pcfreak9000.spaceawaits.core.ecs.content;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;

import de.omnikryptec.event.EventSubscription;
import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.core.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.render.RenderLayers;
import de.pcfreak9000.spaceawaits.world.render.RendererEvents;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderRenderableComponent;

//TODO this system can be generalized to multiple selector entities...
public class SelectorSystem extends EntitySystem {
    
    private Entity selectorEntity;
    
    public SelectorSystem() {
        this.selectorEntity = createSelectorEntity();
    }
    
    @EventSubscription
    private void tsEntityHide(RendererEvents.OpenGuiOverlay ev) {
        Components.RENDER.get(selectorEntity).enabled = false;
    }
    
    @EventSubscription
    private void tsEntityShow(RendererEvents.CloseGuiOverlay ev) {
        Components.RENDER.get(selectorEntity).enabled = true;
    }
    
    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        engine.addEntity(selectorEntity);
    }
    
    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        engine.removeEntity(selectorEntity);
    }
    
    //this class is it: Possibly have a dedicated system for context clues and the selectorentity?
    private Entity createSelectorEntity() {
        Entity e = new EntityImproved();
        RenderComponent rc = new RenderComponent(RenderLayers.WORLD_HUD);
        rc.considerAsGui = true;
        e.add(rc);
        RenderRenderableComponent tex = new RenderRenderableComponent();
        tex.renderable = CoreRes.TILEMARKER_DEF;
        tex.width = 1;
        tex.height = 1;
        tex.color = Color.GRAY;
        e.add(tex);
        e.add(new TransformComponent());
        FollowMouseComponent fmc = new FollowMouseComponent();
        fmc.tiled = true;
        e.add(fmc);
        return e;
    }
}
