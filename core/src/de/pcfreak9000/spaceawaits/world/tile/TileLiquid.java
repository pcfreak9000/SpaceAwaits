package de.pcfreak9000.spaceawaits.world.tile;

import com.badlogic.gdx.math.MathUtils;

import de.pcfreak9000.spaceawaits.util.Direction;
import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderLiquidTransparentMarkerComponent;
import de.pcfreak9000.spaceawaits.world.render.strategy.RenderMarkerComp;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class TileLiquid extends Tile implements IModuleTileEntity {
    
    private static final Direction[] ORDER_DOWN = { Direction.Down, Direction.Left, Direction.Right, Direction.Up };
    private static final Direction[] ORDER_UP = { Direction.Up, Direction.Left, Direction.Right, Direction.Down };
    
    private float flowMin = 0.00001f;//Hmmm. Makes liquids stationary after some time
    
    private float maxValue = 1;
    private float maxComp = 1;//Hmmm. Higher makes the liquid level faster, which is weird
    private float flowSpeed = 1;
    private boolean flowUp = false;
    
    public TileLiquid() {
        addModule(IModuleTileEntity.ID, this);
    }
    
    @Override
    public RenderMarkerComp getRendererMarkerComp() {
        return RenderLiquidTransparentMarkerComponent.INSTANCE;
    }
    
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
    
    public boolean canReplace(Tile t) {
        return true;
    }
    
    @Override
    public boolean canBeReplacedBy(Tile t) {
        return t.isSolid();
    }
    
    @Override
    public ITileEntity createTileEntity(World world, int gtx, int gty, TileLayer layer) {
        return new LiquidState(getMaxValue());
    }
    
    @Override
    public boolean canPlace(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem) {
        return layer == TileLayer.Front;
    }
    
    @Override
    public void onTileSet(int tx, int ty, TileLayer layer, World world, TileSystem tileSystem) {
        super.onTileSet(tx, ty, layer, world, tileSystem);
        tileSystem.scheduleTick(tx, ty, layer, this, 1);//TODO Hmmm. what if at set time its known that this water is in fact settled? e.g. big lakes or oceans...
    }
    
    @Override
    public void onNeighbourChange(World world, TileSystem tileSystem, int gtx, int gty, Tile newNeighbour,
            Tile oldNeighbour, int ngtx, int ngty, TileLayer layer) {
        super.onNeighbourChange(world, tileSystem, gtx, gty, newNeighbour, oldNeighbour, ngtx, ngty, layer);
        tileSystem.scheduleTick(gtx, gty, layer, this, 1);
    }
    
    @Override
    public void updateTick(int tx, int ty, TileLayer layer, World world, TileSystem ts, long tick) {
        super.updateTick(tx, ty, layer, world, ts, tick);
        LiquidState liquiddata = (LiquidState) ts.getTileEntity(tx, ty, layer);
        liquiddata.updateLiquid(tick);
        float myLiquid = liquiddata.getLiquid();
        final float oldliquid = myLiquid;
        Direction[] order = flowUp ? ORDER_UP : ORDER_DOWN;
        for (Direction d : order) {
            if (myLiquid <= LiquidState.MIN_LIQUID) {
                liquiddata.addLiquid(-myLiquid * 2);
                break;
            }
            int i = tx + d.dx;
            int j = ty + d.dy;
            if (world.getBounds().inBounds(i, j)) {
                Tile ne = ts.getTile(i, j, layer);
                if (ne != null && canFlowInto(ne)) {
                    LiquidState neighdata = null;
                    float neLiquid = 0;
                    if (ne == this) {
                        neighdata = (LiquidState) ts.getTileEntity(i, j, layer);
                        neighdata.updateLiquid(tick);
                        neLiquid = neighdata.getLiquid();
                    }
                    float flow = getFlowForDirection(d, neLiquid, myLiquid);
                    if (flow > flowMin) {
                        if (ne != this) {
                            ts.setTile(i, j, layer, this);
                            neighdata = (LiquidState) ts.getTileEntity(i, j, layer);
                            neighdata.setLiquid(0);
                        }
                        flow = MathUtils.clamp(flow, 0, myLiquid);
                        myLiquid -= flow;
                        liquiddata.addLiquid(-flow);
                        neighdata.addLiquid(flow);
                    }
                }
            }
        }
        if (liquiddata.isEmpty()) {
            ts.removeTile(tx, ty, layer);
        } else {
            float dif = oldliquid - myLiquid;
            if (dif != 0) {
                ts.scheduleTick(tx, ty, layer, this, 1);
                for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {
                    ts.scheduleTick(tx + d.dx, ty + d.dy, layer, this, 1);
                }
            }
        }
    }
    
    private boolean canFlowInto(Tile neigh) {
        return neigh == this || (neigh.canBeReplacedBy(this) && this.canReplace(neigh));
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
