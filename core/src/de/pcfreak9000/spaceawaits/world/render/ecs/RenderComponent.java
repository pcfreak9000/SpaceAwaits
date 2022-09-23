package de.pcfreak9000.spaceawaits.world.render.ecs;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.serialize.NBTSerialize;
import de.pcfreak9000.spaceawaits.world.render.strategy.IRenderStrategy;

@NBTSerialize(key = "spaceawaitsRender")
public class RenderComponent implements Component {
    
    @NBTSerialize(key = "e", dBool = true)
    public boolean enabled = true;
    
    @NBTSerialize(key = "l")
    private float layer;//Hmmmmm. this one might need special treatment
    
    public boolean considerAsGui = false;
    
    Array<IRenderStrategy> renderStrategies = new Array<>();
    RenderSystem renSys;
    
    public RenderComponent(float layer) {
        this.layer = layer;
    }
    
    public void setLayer(float layer) {
        this.layer = layer;
        if (renSys != null) {
            renSys.forceLayerSort();
        }
    }
    
    public float getLayer() {
        return layer;
    }
    
    @Deprecated
    public RenderComponent(float layer, String rDecId) {
        this.layer = layer;
        //this.renderStratId = rDecId;
    }
    
}
