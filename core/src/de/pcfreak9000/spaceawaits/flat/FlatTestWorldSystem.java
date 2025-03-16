package de.pcfreak9000.spaceawaits.flat;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.spaceawaits.core.ecs.EntityImproved;
import de.pcfreak9000.spaceawaits.core.ecs.Transferable;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.registry.GameRegistry;
import de.pcfreak9000.spaceawaits.registry.Registry;
import de.pcfreak9000.spaceawaits.world.render.RenderLayers;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderComponent;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderRenderableComponent;

public class FlatTestWorldSystem extends EntitySystem implements Transferable {

    @Override
    public void load() {
        for (int i = 0; i < 150; i++) {
            for (int j = 0; j < 150; j++) {
                TransformComponent tc = new TransformComponent();
                tc.position.set(i, j);
                RenderComponent rc = new RenderComponent(RenderLayers.TILE_FRONT);
                RenderRenderableComponent rec = new RenderRenderableComponent();
                rec.renderable = Registry.TILE_REGISTRY.get("dirt").getTexture();
                rec.color = Color.GREEN;

                rec.width = 1;
                rec.height = 1;
                EntityImproved ei = new EntityImproved();
                ei.add(rec);
                ei.add(rc);
                ei.add(tc);
                getEngine().addEntity(ei);
            }
        }
    }

    @Override
    public void unload() {

    }

}
