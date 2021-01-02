package de.pcfreak9000.spaceawaits.tileworld.light;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.async.AsyncExecutor;

import de.pcfreak9000.spaceawaits.tileworld.World;

public class LightBFPixelAlgorithm {
    
    private AsyncExecutor executor;
    
    public LightBFPixelAlgorithm() {
        executor = new AsyncExecutor(4);
    }
    
    public void submit(World world, Consumer<Pixmap> createTexture, int tx, int ty, int w, int h, Color color) {
        executor.submit(new PixelPointLightTask(world, createTexture, tx, ty, w, h, color));
    }
    
}
