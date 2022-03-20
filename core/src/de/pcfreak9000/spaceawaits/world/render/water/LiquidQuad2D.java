package de.pcfreak9000.spaceawaits.world.render.water;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.utils.Array;
import com.cyphercove.flexbatch.batchable.Quad2D;
import com.cyphercove.flexbatch.utils.AttributeOffsets;

public class LiquidQuad2D extends Quad2D {
    
    private float shoreline;
    private float baseline;
    
    private float distortionStrength;
    private float levelDistortationMod;
    private float levelThickness;
    private float time;
    
    public LiquidQuad2D shore(float l) {
        this.shoreline = l;
        return this;
    }
    
    public LiquidQuad2D base(float b) {
        this.baseline = b;
        return this;
    }
    
    public LiquidQuad2D lvlThickness(float l) {
        this.levelThickness = l;
        return this;
    }
    
    public LiquidQuad2D distortionStrength(float l) {
        this.distortionStrength = l;
        return this;
    }
    
    public LiquidQuad2D levelDistortionModifier(float l) {
        this.levelDistortationMod = l;
        return this;
    }
    
    public LiquidQuad2D time(float t) {
        this.time = t;
        return this;
    }
    
    @Override
    protected void addVertexAttributes(Array<VertexAttribute> attributes) {
        super.addVertexAttributes(attributes);
        VertexAttribute info = new VertexAttribute(Usage.Generic, 2, GL20.GL_FLOAT, false, "a_heightInfo");
        attributes.add(info);
        VertexAttribute info2 = new VertexAttribute(Usage.Generic, 4, GL20.GL_FLOAT, false, "a_anim");
        attributes.add(info2);
    }
    
    @Override
    protected int apply(float[] vertices, int vertexStartingIndex, AttributeOffsets offsets, int vertexSize) {
        super.apply(vertices, vertexStartingIndex, offsets, vertexSize);
        
        int ind = vertexStartingIndex + offsets.generic0;
        for (int i = 0; i < 4; i++) {
            vertices[ind] = shoreline;
            vertices[ind + 1] = baseline;
            ind += vertexSize;
        }
        ind = vertexStartingIndex + offsets.generic1;
        for (int i = 0; i < 4; i++) {
            vertices[ind] = distortionStrength;
            vertices[ind + 1] = levelDistortationMod;
            vertices[ind + 2] = levelThickness;
            vertices[ind + 3] = time;
            ind += vertexSize;
        }
        return 4;
    }
}
