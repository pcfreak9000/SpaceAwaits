package de.pcfreak9000.spaceawaits.core.ecs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Systems implementing this interface run with variable delta time and not with fixed delta time. This is mostly used for renderin.g
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RenderSystemMarker {
    
}
