package de.pcfreak9000.spaceawaits.core.assets;

public interface IGenTexture {
    void setup(int widthTotal, int heightTotal);
    
    void end();
    
    void render(int px, int py, int pw, int ph);
}
