package de.pcfreak9000.spaceawaits.world;

public interface Breakable {//this could just be "class Material" as well
    
    boolean canBreak();//TODO ??????
    
    float getHardness();
    
    float getMaterialLevel();
    
    
}
