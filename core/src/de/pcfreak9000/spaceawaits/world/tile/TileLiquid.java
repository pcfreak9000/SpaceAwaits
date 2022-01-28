package de.pcfreak9000.spaceawaits.world.tile;

import com.badlogic.gdx.math.MathUtils;

import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TileLiquid extends Tile {
    
    private float flowMin = 0;//Hmmm
    
    private float maxValue = 1;
    private float maxComp = 1;
    private float flowSpeed = 1;
    private boolean flowUp = false;//?
    
    public float getMaxValue() {
        return maxValue;
    }
    
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }
    
    public float getMaxComp() {
        return maxComp;
    }
    
    public void setMaxComp(float maxComp) {
        this.maxComp = maxComp;
    }
    
    public float getFlowSpeed() {
        return flowSpeed;
    }
    
    public void setFlowSpeed(float flowSpeed) {
        this.flowSpeed = flowSpeed;
    }
    
    public boolean isFlowUp() {
        return flowUp;
    }
    
    public void setFlowUp(boolean flowUp) {
        this.flowUp = flowUp;
    }
    
    @Override
    public boolean hasMetadata() {
        return true;
    }
    
    @Override
    public IMetadata createMetadata() {
        return new LiquidState();
    }
    
    @Override
    public void onTilePlaced(int tx, int ty, TileLayer layer, World world) {
        super.onTilePlaced(tx, ty, layer, world);
        LiquidState liquid = (LiquidState) world.getSystem(TileSystem.class).getMetadata(tx, ty, layer);
        liquid.addLiquid(getMaxValue());
        //liquid.updateLiquid(100);
        // world.scheduleTick(tx, ty, layer, this);
    }
    
    @Override
    public void onTileSet(int tx, int ty, TileLayer layer, World world) {
        super.onTileSet(tx, ty, layer, world);
        LiquidState liquid = (LiquidState) world.getSystem(TileSystem.class).getMetadata(tx, ty, layer);
        //liquid.addLiquid(getMaxValue());
        //liquid.updateLiquid(100);
        world.scheduleTick(tx, ty, layer, this, 1);
    }
    
    @Override
    public void updateTick(int tx, int ty, TileLayer layer, World world, int tick) {
        super.updateTick(tx, ty, layer, world, tick);
        TileSystem ts = world.getSystem(TileSystem.class);
        LiquidState liquiddata = (LiquidState) ts.getMetadata(tx, ty, layer);
        liquiddata.updateLiquid(tick);
        float myLiquid = liquiddata.getLiquid();
        for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {
            if (myLiquid <= 0) {
                break;
            }
            int i = tx + d.dx;
            int j = ty + d.dy;
            if (world.getBounds().inBounds(i, j)) {
                Tile ne = ts.getTile(i, j, layer);
                if (ne != null && !ne.isSolid()) {//Can flow into neighbour?
                    if (ne != this) {
                        ts.setTile(i, j, layer, this);
                    }
                    LiquidState neighdata = (LiquidState) ts.getMetadata(i, j, layer);
                    neighdata.updateLiquid(tick);
                    float neLiquid = neighdata.getLiquid();
                    float flow = getFlowForDirection(d, neLiquid, myLiquid);
                    if (flow > flowMin) {
                        flow = MathUtils.clamp(flow, 0, myLiquid);
                        myLiquid -= flow;
                        liquiddata.addLiquid(-flow);
                        neighdata.addLiquid(flow);
                        
                    }
                } //Is this tile empty, does it require removal? also neighbours
            }
        }
        if (liquiddata.isEmpty()) {
            ts.setTile(tx, ty, layer, NOTHING);
        } else {
            world.scheduleTick(tx, ty, layer, this, 1);
        }
    }
    
    private float calculateVerticalFlowValue(float remainingLiquid, float neValue) {
        float sum = remainingLiquid + neValue;
        float value = 0;
        
        if (sum <= maxValue) {
            value = maxValue;
        } else if (sum < 2 * maxValue + maxComp) {
            value = (maxValue * maxValue + sum * maxComp) / (maxValue + maxComp);
        } else {
            value = (sum + maxComp) / 2f;
        }
        
        return value;
    }
    
    private float getFlowForDirection(Direction dir, float neighValue, float v) {
        float flow = 0;
        if ((dir == Direction.Down && !flowUp) || (dir == Direction.Up && flowUp)) {
            flow = calculateVerticalFlowValue(v, neighValue) - neighValue;
            if (neighValue > 0 && flow > flowMin) {//war neigh.valueNew vorher, gehts so auch?
                flow *= flowSpeed;
            }
        } else {
            if ((dir == Direction.Up && !flowUp) || (dir == Direction.Down && flowUp)) {
                flow = v - calculateVerticalFlowValue(v, neighValue);
            } else if (dir == Direction.Left) {
                flow = (v - neighValue) / 4f;
            } else if (dir == Direction.Right) {
                flow = (v - neighValue) / 5f;
            }
            if (flow > flowMin) {
                flow *= flowSpeed;
            }
        }
        
        return flow;
    }
}
