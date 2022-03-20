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
    
    public LiquidQuad2D shore(float l) {
        this.shoreline = l;
        return this;
    }
    
    public LiquidQuad2D base(float b) {
        this.baseline = b;
        return this;
    }
    
    @Override
    protected void addVertexAttributes(Array<VertexAttribute> attributes) {
        super.addVertexAttributes(attributes);
        VertexAttribute info = new VertexAttribute(Usage.Generic, 2, GL20.GL_FLOAT, false, "a_shoreline");
        attributes.add(info);
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
        return 4;
    }
}
