package de.pcfreak9000.spaceawaits.world.gen.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.utils.Array;

import de.pcfreak9000.spaceawaits.world.World;
import de.pcfreak9000.spaceawaits.world.tile.Tile;
import de.pcfreak9000.spaceawaits.world.tile.Tile.TileLayer;
import de.pcfreak9000.spaceawaits.world.tile.ecs.TileSystem;

public class StringBasedBlueprint implements Blueprint {
    
    private char[] front;
    private char[] back;
    
    private int height;
    private int width;
    
    private Map<Character, Tile> tilemappings = new HashMap<>();
    
    public void setFront(Object... objects) {
        Data d = convert(objects);
        front = d.chars;
        width = d.width;
        height = d.height;
    }
    
    public void setBack(Object... objects) {
        Data d = convert(objects);
        back = d.chars;
        width = d.width;
        height = d.height;
    }
    
    private static final class Data {
        private char[] chars;
        private int width;
        private int height;
    }
    
    private Data convert(Object... objects) {
        Array<String> lines = new Array<>();
        char c = 0;
        for (Object o : objects) {
            if (o == null) {
                throw new IllegalStateException();
            }
            if (o instanceof String) {
                lines.add((String) o);
            } else if (o instanceof String[]) {
                String[] ar = (String[]) o;
                lines.addAll(ar, 0, ar.length);
            } else if (o instanceof Character) {
                if (c != 0) {
                    throw new IllegalStateException();
                }
                if (c != ' ') {
                    c = (char) o;
                }
            } else if (o instanceof Tile) {
                if (c == 0) {
                    throw new IllegalStateException();
                }
                Tile t = (Tile) o;
                tilemappings.put(c, t);
                c = 0;
            }
        }
        int width = -1;
        for (String s : lines) {
            if (width == -1) {
                width = s.length();
            } else if (width != s.length()) {
                throw new IllegalArgumentException();
            }
        }
        int height = lines.size;
        char[] chara = new char[width * height];
        int ind = 0;
        for (String s : lines) {
            for (char ca : s.toCharArray()) {
                if (ca != ' ' && !tilemappings.containsKey(ca)) {
                    throw new IllegalStateException();
                }
                chara[ind] = ca;
                ind++;
            }
        }
        Data dat = new Data();
        dat.chars = chara;
        dat.width = width;
        dat.height = height;
        return dat;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    private int indexOf(int rx, int ry) {
        if (rx < 0 || rx >= width || ry < 0 || ry >= height) {
            return -1;
        }
        return rx + (height - ry - 1) * width;
    }
    
    @Override
    public void generate(TileSystem tiles, World world, int txs, int tys, int rxs, int rys, int width, int height,
            Random random) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rx = rxs + i;
                int ry = rys + j;
                int index = indexOf(rx, ry);
                if (index == -1) {
                    continue;
                }
                char frontchar = front[index];
                char backchar = back[index];
                int tx = txs + rx;
                int ty = tys + ry;
                if (frontchar != ' ') {
                    Tile t = tilemappings.get(frontchar);
                    tiles.setTile(tx, ty, TileLayer.Front, t);
                }
                if (backchar != ' ') {
                    Tile t = tilemappings.get(backchar);
                    tiles.setTile(tx, ty, TileLayer.Back, t);
                }
            }
        }
    }
    
}