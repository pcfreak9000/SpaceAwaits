package de.pcfreak9000.spaceawaits.mod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.pcfreak9000.spaceawaits.core.SpaceAwaits;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {
    
    String id();
    
    String name();
    
    long[] version();
    
    String resourceLocation() default "res";
    
    String[] se2dversion() default { SpaceAwaits.VERSION };
    
    boolean accessible() default true;
}