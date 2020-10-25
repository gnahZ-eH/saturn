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
 * The edm:EntityType element represents an entity type in the entity model.
 *
 * It contains zero or more edm:Property and edm:NavigationProperty elements
 * describing the properties if the entity type.
 *
 * It MAY contain one edm:Key element.
 *
 * For more details:
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793930
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataEntityType {

    /**
     * The edm:EntityType element MUST include a Name attribute whose value is a SimpleIdentifier.
     * The name MUST be unique within its namespace.
     *
     * @return name of the EntityType.
     */
    String name() default "";

    /**
     * A schema is identified by a Namespace. All edm:Schema elements MUST have a Namespace defined
     * through a Namespace attribute which MUST be unique within the document, and SHOULD be globally
     * unique. A schema cannot span more than one document.
     *
     *  @return name of the Namespace.
     */
    String namespace() default "";

    /**
     * An entity is uniquely identified within an entity set by its key. An entity type that is not
     * abstract MUST either contain exactly one edm:Key element or inherit its key from its base type.
     * An abstract entity type MAY define a key if it doesn’t inherit one.
     *
     * An entity type’s key refers to the set of properties that uniquely identify an instance of the
     * entity type within an entity set.
     *
     * The edm:Key element MUST contain at least one edm:PropertyRef element. An edm:PropertyRef element
     * references an edm:Property.
     *
     * @return keys of the EntityType.
     */
    String[] keys() default { };

    /**
     * For JPA entity, since the jpa entity and edm entity are combine with each other
     * So this value should always be the Class with this annotation.
     * @return entity of edm(jpa).
     */
    Class<?> jpaEntity() default Object.class;

    /**
     * The variable of edm(jpa) entity, default change the first letter of entity class to lower case.
     * @return variable of edm(jpa) entity.
     */
    String jpaVariable() default "";

    /**
     * One ODataEntityType may extend another ODataEntityType.
     * In JPA entity, should have one field of this ODataEntityType.
     * @return the field name of the extended edm(jpa) entity.
     */
    String superEntityName() default "";
}
