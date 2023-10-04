package de.pcfreak9000.spaceawaits.core.assets;

public interface IGenTexture {
    void setup(int patchWidthMax, int patchHeightMax);
    
    void end();
    
    void render(int pi, int pj, int width, int height);
}
