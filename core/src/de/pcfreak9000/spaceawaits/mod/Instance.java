package de.pcfreak9000.spaceawaits.mod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Instance {
    
    String id() default Modloader.THIS_INSTANCE_ID;
    
    long[] requiredVersion() default {};
}
