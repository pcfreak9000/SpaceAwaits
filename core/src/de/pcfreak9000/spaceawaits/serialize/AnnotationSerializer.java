package de.pcfreak9000.spaceawaits.serialize;

import java.lang.reflect.Field;

import de.pcfreak9000.nbt.NBTCompound;

public class AnnotationSerializer {
    private static final NBTCompound EMPTY_NONNULL = new NBTCompound();
    
    public static boolean canAnnotationSerialize(Object ser) {
        return ser.getClass().isAnnotationPresent(NBTSerialize.class);
    }
    
    public static void serialize(NBTCompound parent, Object ser) {
        NBTSerialize classan = ser.getClass().getAnnotation(NBTSerialize.class);
        if (classan == null) {
            throw new IllegalArgumentException();
        }
        if (classan.key().isBlank()) {
            throw new IllegalStateException("Blank keys are not allowed");
        }
        //String prefix = classan.key();
        NBTCompound mycompound = new NBTCompound();
        serializeAnnotatedFields(mycompound, ser, "");
        if (ser instanceof INBTSerializable) {
            INBTSerializable hehe = (INBTSerializable) ser;
            hehe.writeNBT(mycompound);
        }
        parent.putCompound(classan.key(), mycompound);
    }
    
    public static void deserialize(NBTCompound parent, Object ser) {
        NBTSerialize classan = ser.getClass().getAnnotation(NBTSerialize.class);
        if (classan == null) {
            throw new IllegalArgumentException();
        }
        if (classan.key().isBlank()) {
            throw new IllegalStateException("Blank keys are not allowed");
        }
        //String prefix = classan.key();
        NBTCompound mycompound = parent.getCompoundOrDefault(classan.key(), EMPTY_NONNULL);
        deserializeAnnotatedFields(mycompound, ser, "");
        if (ser instanceof INBTSerializable) {
            INBTSerializable hehe = (INBTSerializable) ser;
            hehe.readNBT(mycompound);
        }
    }
    
    private static void serializeAnnotatedFields(NBTCompound nbt, Object ser, String prefix) {
        Field[] fields = ser.getClass().getFields();
        for (Field f : fields) {
            NBTSerialize an = f.getAnnotation(NBTSerialize.class);
            if (an != null) {
                if (!f.getType().isPrimitive()) {
                    throw new IllegalStateException("Cant serialize complex types");
                }
                if (an.key().isBlank()) {
                    throw new IllegalStateException("Blank keys are not allowed");
                }
                String key = prefix + an.key();
                Class<?> type = f.getType();
                try {
                    if (type == Integer.TYPE) {
                        nbt.putIntegerSmart(key, f.getInt(ser));
                    } else if (type == Long.TYPE) {
                        nbt.putIntegerSmart(key, f.getLong(ser));
                    } else if (type == Short.TYPE) {
                        nbt.putIntegerSmart(key, f.getShort(ser));
                    } else if (type == Byte.TYPE) {
                        nbt.putIntegerSmart(key, f.getByte(ser));
                    } else if (type == Boolean.TYPE) {
                        nbt.putBooleanAsByte(key, f.getBoolean(ser));
                    } else if (type == Float.TYPE) {
                        nbt.putFloat(key, f.getFloat(ser));
                    } else if (type == Double.TYPE) {
                        nbt.putDouble(key, f.getDouble(ser));
                    } else {
                        throw new IllegalArgumentException("Type not supported: " + type);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    
    private static void deserializeAnnotatedFields(NBTCompound nbt, Object ser, String prefix) {
        Field[] fields = ser.getClass().getFields();
        for (Field f : fields) {
            NBTSerialize an = f.getAnnotation(NBTSerialize.class);
            if (an != null) {
                if (!f.getType().isPrimitive()) {
                    throw new IllegalStateException("Cant serialize complex types");
                }
                if (an.key().isBlank()) {
                    throw new IllegalStateException("Blank keys are not allowed");
                }
                String key = prefix + an.key();
                Class<?> type = f.getType();
                try {
                    if (type == Integer.TYPE) {
                        f.setInt(ser, (int) nbt.getIntegerSmartOrDefault(key, an.dInt()));
                    } else if (type == Long.TYPE) {
                        f.setLong(ser, nbt.getIntegerSmartOrDefault(key, an.dLong()));
                    } else if (type == Short.TYPE) {
                        f.setShort(ser, (short) nbt.getIntegerSmartOrDefault(key, an.dShort()));
                    } else if (type == Byte.TYPE) {
                        f.setByte(ser, (byte) nbt.getIntegerSmartOrDefault(key, an.dByte()));
                    } else if (type == Boolean.TYPE) {
                        f.setBoolean(ser, nbt.getBooleanFromByteOrDefault(key, an.dBool()));
                    } else if (type == Float.TYPE) {
                        f.setFloat(ser, nbt.getFloatOrDefault(key, an.dFloat()));
                    } else if (type == Double.TYPE) {
                        f.setDouble(ser, nbt.getDoubleOrDefault(key, an.dDouble()));
                    } else {
                        throw new IllegalArgumentException("Type not supported: " + type);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
