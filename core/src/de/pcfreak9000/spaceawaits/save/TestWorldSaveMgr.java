package de.pcfreak9000.spaceawaits.save;

import java.util.List;

import de.pcfreak9000.nbt.NBTCompound;

public class TestWorldSaveMgr implements ISaveManager {
    
    @Override
    public boolean exists(String foldername) {
        return false;
    }
    
    @Override
    public ISave createSave(String name, long seed) {
        return new ISave() {
            
            @Override
            public void writePlayerNBT(NBTCompound nbtc) {
            }
            
            @Override
            public NBTCompound readPlayerNBT() {
                return null;
            }
            
            @Override
            public IWorldSave getWorld(String uuid) {
                return new IWorldSave() {
                    
                    @Override
                    public void writeGlobal(NBTCompound nbtc) {
                    }
                    
                    @Override
                    public void writeChunk(int cx, int cy, NBTCompound nbtc) {
                    }
                    
                    @Override
                    public NBTCompound readGlobal() {
                        return null;
                    }
                    
                    @Override
                    public NBTCompound readChunk(int cx, int cy) {
                        return null;
                    }
                    
                    @Override
                    public boolean hasChunk(int cx, int cy) {
                        return false;
                    }
                    
                    @Override
                    public WorldMeta getWorldMeta() {
                        return null;
                    }
                    
                    @Override
                    public boolean hasGlobal() {
                        return false;
                    }
                };
            }
            
            @Override
            public SaveMeta getSaveMeta() {
                return null;
            }
            
            @Override
            public String createWorld(String name, WorldMeta m) {
                return "";
            }
            
            @Override
            public boolean hasPlayer() {
                return false;
            }
            
            @Override
            public boolean hasWorld(String uuid) {
                return false;
            }
        };
    }
    
    @Override
    public void deleteSave(String foldername) {
    }
    
    @Override
    public ISave getSave(String foldername) {
        return new ISave() {
            
            @Override
            public void writePlayerNBT(NBTCompound nbtc) {
            }
            
            @Override
            public NBTCompound readPlayerNBT() {
                return null;
            }
            
            @Override
            public IWorldSave getWorld(String uuid) {
                return new IWorldSave() {
                    
                    @Override
                    public void writeGlobal(NBTCompound nbtc) {
                    }
                    
                    @Override
                    public void writeChunk(int cx, int cy, NBTCompound nbtc) {
                    }
                    
                    @Override
                    public NBTCompound readGlobal() {
                        return null;
                    }
                    
                    @Override
                    public NBTCompound readChunk(int cx, int cy) {
                        return null;
                    }
                    
                    @Override
                    public boolean hasChunk(int cx, int cy) {
                        return false;
                    }
                    
                    @Override
                    public WorldMeta getWorldMeta() {
                        return null;
                    }
                    
                    @Override
                    public boolean hasGlobal() {
                        return false;
                    }
                };
            }
            
            @Override
            public SaveMeta getSaveMeta() {
                return null;
            }
            
            @Override
            public String createWorld(String name, WorldMeta m) {
                return "";
            }
            
            @Override
            public boolean hasPlayer() {
                return false;
            }
            
            @Override
            public boolean hasWorld(String uuid) {
                return false;
            }
        };
    }
    
    @Override
    public List<SaveMeta> listSaves() {
        return null;
    }
    
}
