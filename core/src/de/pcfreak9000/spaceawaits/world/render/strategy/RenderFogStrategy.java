package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.utils.Disposable;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderFogComponent;

public class RenderFogStrategy extends AbstractRenderStrategy implements Disposable {
    
    private Mesh mesh;
    
    public RenderFogStrategy() {
        super(Family.all(RenderFogComponent.class).get());
        mesh = new Mesh(false, 4, 6, new VertexAttribute(Usage.Position, 2, "pos"));
        mesh.setVertices(new float[] { 0, 0, 100, 0, 0, 100, 100, 100 });
        mesh.setIndices(new short[] { 0, 1, 3, 0, 3, 2 });
    }
    
    @Override
    public void render(Entity e, float dt) {
        CoreRes.FOG_SHADER.getShader().bind();
        mesh.render(CoreRes.FOG_SHADER.getShader(), GL20.GL_TRIANGLES);
    }
    
    @Override
    public void dispose() {
        mesh.dispose();
    }
    
}
