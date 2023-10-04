package de.pcfreak9000.spaceawaits.serialize;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ FIELD, TYPE })
public @interface NBTSerialize {
    
    //option to not write if its the default?? this is kinda already done, defaults aren't saved except if disableDefaults is true
    
    String key();
    
    boolean disableDefaults() default false;
    
    long dLong() default 0;
    
    int dInt() default 0;
    
    short dShort() default 0;
    
    byte dByte() default 0;
    
    float dFloat() default 0;
    
    double dDouble() default 0;
    
    boolean dBool() default false;
    
    String dString() default "";
}
