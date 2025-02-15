package de.pcfreak9000.spaceawaits.world.render.strategy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

import de.pcfreak9000.spaceawaits.core.assets.CoreRes;
import de.pcfreak9000.spaceawaits.core.assets.ShaderProvider;
import de.pcfreak9000.spaceawaits.core.ecs.content.TransformComponent;
import de.pcfreak9000.spaceawaits.core.screen.GameScreen;
import de.pcfreak9000.spaceawaits.world.ecs.Components;
import de.pcfreak9000.spaceawaits.world.render.ecs.CameraSystem;
import de.pcfreak9000.spaceawaits.world.render.ecs.RenderFogComponent;

public class RenderFogStrategy extends AbstractRenderStrategy implements Disposable {
    
    private Mesh mesh;
    private GameScreen renderer;
    private ShaderProvider shader = CoreRes.FOG_SHADER;
    
    public RenderFogStrategy(GameScreen renderer) {
        super(Family.all(RenderFogComponent.class, TransformComponent.class).get());
        this.renderer = renderer;
        mesh = new Mesh(false, 4, 6, new VertexAttribute(Usage.Position, 2, "pos"));
        mesh.setIndices(new short[] { 0, 1, 3, 0, 3, 2 });
        
        ShaderProgram.pedantic = false;
        shader.getShader().bind();
        //System.out.println(shader.getShader().getLog());
    }
    
    private float[] vertices = new float[8];
    
    @Override
    public void begin() {
        super.begin();
        renderer.setDefaultBlending();
        renderer.applyViewport();
    }
    
    @Override
    public void render(Entity e, float dt) {
        RenderFogComponent rfc = Components.RENDER_FOG.get(e);
        TransformComponent tc = Components.TRANSFORM.get(e);
        Camera cam = getEngine().getSystem(CameraSystem.class).getCamera();
        shader.getShader().bind();
        shader.getShader().setUniformMatrix("u_projView", cam.combined);
        shader.getShader().setUniformf("time", renderer.getRenderTime());
        float xmin, ymin, xmax, ymax;
        if (rfc.oversize) {//TODO test is this affects performance or if this is a useless optimization
            xmin = cam.position.x - cam.viewportWidth / 2;
            ymin = cam.position.y - cam.viewportHeight / 2;
            xmax = cam.position.x + cam.viewportWidth / 2;
            ymax = cam.position.y + cam.viewportHeight / 2;
        } else {
            xmin = tc.position.x;
            ymin = tc.position.y;
            xmax = xmin + rfc.width;
            ymax = ymin + rfc.height;
        }
        vertices[0] = xmin;
        vertices[1] = ymin;
        vertices[2] = xmax;
        vertices[3] = ymin;
        vertices[4] = xmin;
        vertices[5] = ymax;
        vertices[6] = xmax;
        vertices[7] = ymax;
        mesh.setVertices(vertices);
        shader.getShader().setUniformf("rectOuter", tc.position.x, tc.position.y, tc.position.x + rfc.width,
                tc.position.y + rfc.height);
        shader.getShader().setUniformf("color", rfc.color);
        shader.getShader().setUniformf("vel", rfc.velx, rfc.vely);
        shader.getShader().setUniformf("rectInner", rfc.innerRect.x, rfc.innerRect.y,
                rfc.innerRect.x + rfc.innerRect.width, rfc.innerRect.y + rfc.innerRect.height);
        shader.getShader().setUniformf("scales", rfc.scalex, rfc.scaley);
        shader.getShader().setUniformf("finalCoeff", rfc.coeffa, rfc.coeffb);
        shader.getShader().setUniformf("fbmVel", rfc.fbmVelx, rfc.fbmVely);
        mesh.render(shader.getShader(), GL20.GL_TRIANGLES);
    }
    
    @Override
    public void dispose() {
        mesh.dispose();
    }
    
}
