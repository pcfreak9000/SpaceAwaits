package de.pcfreak9000.spaceawaits.serialize;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import de.omnikryptec.util.Logger;
import de.pcfreak9000.nbt.NBTCompound;
import de.pcfreak9000.nbt.NBTTypeException;

public class AnnotationSerializer {
    private static final NBTCompound EMPTY_NONNULL = new NBTCompound();
    private static final Logger LOGGER = Logger.getLogger(AnnotationSerializer.class);
    
    public static boolean canAnnotationSerialize(Object ser) {
        return ser.getClass().isAnnotationPresent(NBTSerialize.class);
    }
    
    public static NBTCompound serialize(Object ser) {
        NBTCompound mycompound = new NBTCompound();
        serializeAnnotatedFields(mycompound, ser);
        if (ser instanceof INBTSerializable) {
            INBTSerializable hehe = (INBTSerializable) ser;
            hehe.writeNBT(mycompound);
        } else if (ser instanceof NBTSerializable) {
            NBTSerializable hehe2 = (NBTSerializable) ser;
            mycompound.put("c", hehe2.writeNBT());
        }
        return mycompound;
    }
    
    public static void serializeInto(NBTCompound parent, Object ser) {
        NBTSerialize classan = ser.getClass().getAnnotation(NBTSerialize.class);
        if (classan == null) {
            throw new IllegalArgumentException();
        }
        if (classan.key().isBlank()) {
            throw new IllegalStateException("Blank keys are not allowed");
        }
        parent.put(classan.key(), serialize(ser));
    }
    
    public static void deserialize(Object ser, NBTCompound mycompound) {
        deserializeAnnotatedFields(mycompound, ser);
        if (ser instanceof INBTSerializable) {
            INBTSerializable hehe = (INBTSerializable) ser;
            hehe.readNBT(mycompound);
        } else if (ser instanceof NBTSerializable) {
            NBTSerializable hehe2 = (NBTSerializable) ser;
            hehe2.readNBT(mycompound.get("c"));
        }
    }
    
    public static void deserializeFrom(NBTCompound parent, Object ser) {
        NBTSerialize classan = ser.getClass().getAnnotation(NBTSerialize.class);
        if (classan == null) {
            throw new IllegalArgumentException();
        }
        if (classan.key().isBlank()) {
            throw new IllegalStateException("Blank keys are not allowed");
        }
        NBTCompound mycompound = parent.getCompoundOrDefault(classan.key(), EMPTY_NONNULL);
        deserialize(ser, mycompound);
    }
    
    private static void serializeAnnotatedFields(NBTCompound nbt, Object ser) {
        Field[] fields = ser.getClass().getDeclaredFields();//TODO super fields
        for (Field f : fields) {
            NBTSerialize an = f.getAnnotation(NBTSerialize.class);
            if (an != null) {
                if (Modifier.isStatic(f.getModifiers())) {
                    LOGGER.warn("Static fields are not serialized: " + f);
                    continue;
                }
                try {
                    f.setAccessible(true);
                } catch (Exception e) {
                    LOGGER.warn("Couldn't set accessible: " + f);
                    e.printStackTrace();
                    continue;
                }
                if (an.key().isBlank()) {
                    throw new IllegalStateException("Blank keys are not allowed");
                }
                if (!f.getType().isPrimitive()) {
                    throw new IllegalStateException("Cant serialize complex types");
                }
                String key = an.key();
                Class<?> type = f.getType();
                try {
                    if (type == Integer.TYPE) {
                        int innt = f.getInt(ser);
                        if (innt != an.dInt() || an.disableDefaults()) {
                            nbt.putIntegerSmart(key, innt);
                        }
                    } else if (type == Long.TYPE) {
                        long loong = f.getLong(ser);
                        if (loong != an.dLong() || an.disableDefaults()) {
                            nbt.putIntegerSmart(key, loong);
                        }
                    } else if (type == Short.TYPE) {
                        short shortt = f.getShort(ser);
                        if (shortt != an.dShort() || an.disableDefaults()) {
                            nbt.putIntegerSmart(key, shortt);
                        }
                    } else if (type == Byte.TYPE) {
                        byte bytt = f.getByte(ser);
                        if (bytt != an.dByte() || an.disableDefaults()) {
                            nbt.putIntegerSmart(key, bytt);
                        }
                    } else if (type == Boolean.TYPE) {
                        boolean bool = f.getBoolean(ser);
                        if (bool != an.dBool() || an.disableDefaults()) {
                            nbt.putBooleanAsByte(key, bool);
                        }
                    } else if (type == Float.TYPE) {
                        float floot = f.getFloat(ser);
                        if (floot != an.dFloat() || an.disableDefaults()) {
                            nbt.putFloat(key, floot);
                        }
                    } else if (type == Double.TYPE) {
                        double dooble = f.getDouble(ser);
                        if (dooble != an.dDouble() || an.disableDefaults()) {
                            nbt.putDouble(key, dooble);
                        }
                    } else {
                        throw new IllegalArgumentException("Type not supported: " + type);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    
    //TODO if the Fields type is Serializable just serialize that?? and deserialization???
    //TODO enums can be saved as ints/ids, strings can be saved
    private static void deserializeAnnotatedFields(NBTCompound nbt, Object ser) {
        Field[] fields = ser.getClass().getDeclaredFields();//TODO super fields
        for (Field f : fields) {
            NBTSerialize an = f.getAnnotation(NBTSerialize.class);
            if (an != null) {
                if (Modifier.isStatic(f.getModifiers())) {
                    LOGGER.warn("Static fields are not serialized: " + f);
                    continue;
                }
                try {
                    f.setAccessible(true);
                } catch (Exception e) {
                    LOGGER.warn("Couldn't set accessible: " + f);
                    e.printStackTrace();
                    continue;
                }
                if (an.key().isBlank()) {
                    throw new IllegalStateException("Blank keys are not allowed");
                }
                if (!f.getType().isPrimitive()) {
                    throw new IllegalStateException("Cant serialize complex types");
                }
                String key = an.key();
                Class<?> type = f.getType();
                try {
                    if (type == Integer.TYPE) {
                        long innt = an.disableDefaults() ? nbt.getIntegerSmart(key)
                                : nbt.getIntegerSmartOrDefault(key, an.dInt());
                        f.setInt(ser, (int) innt);
                    } else if (type == Long.TYPE) {
                        long loong = an.disableDefaults() ? nbt.getIntegerSmart(key)
                                : nbt.getIntegerSmartOrDefault(key, an.dLong());
                        f.setLong(ser, loong);
                    } else if (type == Short.TYPE) {
                        long shortt = an.disableDefaults() ? nbt.getIntegerSmart(key)
                                : nbt.getIntegerSmartOrDefault(key, an.dShort());
                        f.setShort(ser, (short) shortt);
                    } else if (type == Byte.TYPE) {
                        long bytt = an.disableDefaults() ? nbt.getIntegerSmart(key)
                                : nbt.getIntegerSmartOrDefault(key, an.dByte());
                        f.setByte(ser, (byte) bytt);
                    } else if (type == Boolean.TYPE) {
                        boolean bool = an.disableDefaults() ? nbt.getBooleanFromByte(key)
                                : nbt.getBooleanFromByteOrDefault(key, an.dBool());
                        f.setBoolean(ser, bool);
                    } else if (type == Float.TYPE) {
                        float floot = an.disableDefaults() ? nbt.getFloat(key)
                                : nbt.getFloatOrDefault(key, an.dFloat());
                        f.setFloat(ser, floot);
                    } else if (type == Double.TYPE) {
                        double dooble = an.disableDefaults() ? nbt.getDouble(key)
                                : nbt.getDoubleOrDefault(key, an.dDouble());
                        f.setDouble(ser, dooble);
                    } else {
                        throw new IllegalArgumentException("Type not supported: " + type);
                    }
                } catch (NBTTypeException ex) {
                    if (an.disableDefaults()) {
                        new RuntimeException("Can't fall back to defaults since they are disabled", ex);
                    }
                    try {
                        if (type == Integer.TYPE) {
                            f.setInt(ser, an.dInt());
                        } else if (type == Long.TYPE) {
                            f.setLong(ser, an.dLong());
                        } else if (type == Short.TYPE) {
                            f.setShort(ser, an.dShort());
                        } else if (type == Byte.TYPE) {
                            f.setByte(ser, an.dByte());
                        } else if (type == Boolean.TYPE) {
                            f.setBoolean(ser, an.dBool());
                        } else if (type == Float.TYPE) {
                            f.setFloat(ser, an.dFloat());
                        } else if (type == Double.TYPE) {
                            f.setDouble(ser, an.dDouble());
                        } else {
                            throw new IllegalArgumentException("Type not supported: " + type);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
