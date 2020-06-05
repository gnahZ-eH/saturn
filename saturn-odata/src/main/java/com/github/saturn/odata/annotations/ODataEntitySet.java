package com.github.saturn.odata.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an entity set for an entity type. This annotation must only be used on classes that also have an
 * {@code EdmEntity} annotation.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataEntitySet {

    /**
     * The name of the entity set. If the name of the entity set is not specified using either this attribute or the
     * {@code name} attribute, the automatically pluralized name of the entity is used.
     *
     * @return The name of the entity set.
     */
    String name() default "";

    /**
     * The container that the entity set has. If not specified, the namespace of the entity set is used.
     * NOTE: The container of the first entity class will be used for the whole entity data model. In case where
     * other entity classes have container different than the first one, this container will not be taken into
     * account.
     *
     * @return container of the entity set.
     */
    String container() default "";

    /**
     * Specifies whether this entity set should be included in the service document.
     *
     * @return {@code true} if this entity set should be included in the service document, {@code false} otherwise.
     */
    boolean includedInServiceDocument() default true;

}
