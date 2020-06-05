package com.github.saturn.odata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Annotation for a EDM/CSDL EntityType element.</p>
 * <p>EdmEntityType holds a set of related information like EdmPrimitiveType, EdmComplexType
 * and EdmNavigation properties.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataEntityType {

    /**
     * Define the name for the EDM EntityType.
     * If not set a default value has to be generated by the EDM provider, the name of the class is used.
     *
     * @return The name of the entity type.
     */
    String name() default "";

    /**
     * Define the namespace for the EDM EntityType.
     * If not set a default value has to be generated by the EDM provider, the name of the package that contains
     * the entity class is used.
     *
     * @return namespace for the EDM EntityType
     */
    String namespace() default "";

    /**
     * The key of the entity, consisting of an array of the names of properties that form the key. This attribute is
     * provided for convenience, which can be used instead of the more verbose {@code keyRef}. An entity must have a
     * key which consists of at least one of the properties of the entity; either {@code key} or {@code keyRef} must
     * be specified on an entity.
     *
     * @return The key of the entity.
     */
    String[] keys() default { };

}
