package de.pcfreak9000.spaceawaits.util;

import java.io.File;
import java.io.InputStream;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;

public class FileHandleClassLoaderExtension extends FileHandle {
    
    private ClassLoader[] resSrcs;
    private String name;
    
    public FileHandleClassLoaderExtension(String name, ClassLoader... classloaders) {
        this.resSrcs = classloaders;
        this.name = name;
        super.file = new File(name);
        super.type = FileType.Classpath;
    }
    
    @Override
    public InputStream read() {
        InputStream in = null;
        try {
            in = super.read();
        } catch (Exception e) {
        }
        for (int i = 0; in == null && i < resSrcs.length; i++) {
            try {
                in = resSrcs[i].getResourceAsStream(name);
            } catch (Exception e) {
            }
        }
        if (in == null) {
            throw new RuntimeException("Error reading file: " + name);
        }
        return in;
    }
    
}
