/*
 * MIT License
 *
 * Copyright (c) [2020] [He Zhang]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished
 *  to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.saturn.odata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A navigation property allows navigation to related entities.
 *
 * For more details:
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793918
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ODataNavigationProperty {

    /**
     * The edm:NavigationProperty element MUST include a Name attribute whose value is a SimpleIdentifier
     * that is used when navigating from the structured type that declares the navigation property to the
     * related entity type.
     *
     * The name of the navigation property MUST be unique within the set of structural and navigation
     * properties of the containing structured type and any of its base types.
     *
     * @return name of the NavigationProperty.
     */
    String name() default "";

    /**
     * The edm:NavigationProperty element MUST include a Type attribute. The value of the type attribute
     * MUST resolve to an entity type or a collection of an entity type declared in the same document or
     * a document referenced with an edmx:Reference element, or the abstract type Edm.EntityType.
     *
     * If the value is an entity type name, there can be at most one related entity. If it is a collection,
     * an arbitrary number of entities can be related.
     *
     * The related entities MUST be of the specified entity type or one of its subtypes.
     *
     * @return type of the NavigationProperty.
     */
    String type() default "";

    /**
     * The edm:NavigationProperty element MAY contain the Nullable attribute whose Boolean value specifies
     * whether a navigation target is required for the navigation property.
     *
     * If no value is specified for a navigation property whose Type attribute does not specify a collection,
     * the Nullable attribute defaults to true. The value true (or the absence of the Nullable attribute)
     * indicates that no navigation target is required. The value false indicates that a navigation target is
     * required for the navigation property on instances of the containing type.
     *
     * A navigation property whose Type attribute specifies a collection MUST NOT specify a value for the
     * Nullable attribute as the collection always exists, it may just be empty.
     */
    boolean nullable() default true;

    /**
     * A navigation property of an entity type MAY specify a navigation property path value for the Partner attribute.
     *
     * This attribute MUST NOT be specified for navigation properties of complex types.
     *
     * If specified, the value of this attribute MUST be a path from the entity type specified in the Type attribute
     * to a navigation property defined on that type or a derived type. The path may traverse complex types,
     * including derived complex types, but MUST NOT traverse any navigation properties. The type of the partner
     * navigation property MUST be the containing entity type of the current navigation property or one of its parent
     * entity types.
     *
     * If the Partner attribute identifies a single-valued navigation property, the partner navigation property
     * MUST lead back to the source entity from all related entities. If the Partner attribute identifies a
     * multi-valued navigation property, the source entity MUST be part of that collection.
     *
     * If no partner navigation property is specified, no assumptions can be made as to whether one of the
     * navigation properties on the target type will lead back to the source entity.
     *
     * If a partner navigation property is specified, this partner navigation property MUST either specify
     * the current navigation property as its partner to define a bi-directional relationship or it MUST NOT
     * specify a partner attribute. The latter can occur if the partner navigation property is defined on a
     * complex type or the current navigation property is defined on a type derived from the type of the partner
     * navigation property.
     */
    String partner() default "";

    /**
     * For JPA entity, since the jpa entity and edm entity are combine with each other
     * So this value should always be the value type with this annotation.
     * @return entity of edm(jpa).
     */
    Class<?> jpaEntity() default Object.class;
}
