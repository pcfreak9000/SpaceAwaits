package de.pcfreak9000.spaceawaits.world.render.water;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.utils.Array;
import com.cyphercove.flexbatch.batchable.Quad2D;
import com.cyphercove.flexbatch.utils.AttributeOffsets;

public class LiquidQuad2D extends Quad2D {
    
    private float shoreline;
    
    @Override
    protected void addVertexAttributes(Array<VertexAttribute> attributes) {
        super.addVertexAttributes(attributes);
        VertexAttribute info = new VertexAttribute(Usage.Generic, 1, GL20.GL_FLOAT, false, "a_shoreline");
        attributes.add(info);
    }
    
    @Override
    protected int apply(float[] vertices, int vertexStartingIndex, AttributeOffsets offsets, int vertexSize) {
        super.apply(vertices, vertexStartingIndex, offsets, vertexSize);
        
        int ind = vertexStartingIndex + offsets.generic0;
        vertices[ind] = shoreline;
        vertices[ind + vertexSize] = shoreline;
        vertices[ind + vertexSize] = shoreline;
        vertices[ind + vertexSize] = shoreline;
        return 4;
    }
}
