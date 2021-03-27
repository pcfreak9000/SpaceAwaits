package de.pcfreak9000.spaceawaits.save;

import java.io.File;

public class SaveUtils {
    static long getLastModified(File dir) {
        long l = 0;
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                l = Math.max(l, getLastModified(f));
            }
        } else {
            l = dir.lastModified();
        }
        return l;
    }
}
