package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

import de.pcfreak9000.spaceawaits.core.CoreRes;
import de.pcfreak9000.spaceawaits.core.ShaderProvider;
import de.pcfreak9000.spaceawaits.world.render.GameRenderer;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderFogComponent;

public class RenderFogStrategy extends AbstractRenderStrategy implements Disposable {
    
    private Mesh mesh;
    private GameRenderer renderer;
    private ShaderProvider shader = CoreRes.FOG_SHADER;
    
    public RenderFogStrategy(GameRenderer renderer) {
        super(Family.all(RenderFogComponent.class).get());
        this.renderer = renderer;
        mesh = new Mesh(false, 4, 6, new VertexAttribute(Usage.Position, 2, "pos"));
        mesh.setIndices(new short[] { 0, 1, 3, 0, 3, 2 });
        float xmin = -1, ymin = -1, xmax = 1, ymax = 1;
        mesh.setVertices(new float[] { xmin, ymin, xmax, ymin, xmin, ymax, xmax, ymax });
        ShaderProgram.pedantic = false;
        shader.getShader().bind();
        System.out.println(shader.getShader().getLog());
        shader.getShader().setUniformf("vel", -1, -1);
        shader.getShader().setUniformf("color", Color.CYAN);
    }
    
    float time = 0;
    
    @Override
    public void render(Entity e, float dt) {
        renderer.setDefaultBlending();
        //      Gdx.gl.glEnable(GL20.GL_BLEND);
        //        Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        renderer.applyViewport();
        Camera cam = renderer.getCurrentView().getCamera();
        shader.getShader().bind();
        shader.getShader().setUniformf("corners", cam.position.x - cam.viewportWidth / 2,
                cam.position.y - cam.viewportHeight / 2, cam.position.x + cam.viewportWidth / 2,
                cam.position.y + cam.viewportHeight / 2);
        time += dt;
        shader.getShader().setUniformf("time", time);
        mesh.render(CoreRes.FOG_SHADER.getShader(), GL20.GL_TRIANGLES);
    }
    
    @Override
    public void dispose() {
        mesh.dispose();
    }
    
}
