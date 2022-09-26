package de.pcfreak9000.spaceawaits.save;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

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
    
    public static void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.delete(path);
    }
}
