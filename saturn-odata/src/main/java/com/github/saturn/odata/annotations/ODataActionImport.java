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
 * The edm:ActionImport element allows exposing an unbound action as a top-level element in an entity
 * container. Action imports are never advertised in the service document.
 *
 * For more details:
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793994
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataActionImport {

    /**
     * The edm:ActionImport element MUST include a Name attribute whose value is a SimpleIdentifier.
     * It MAY be identical to the last segment of the QualifiedName used to specify the Action attribute value.
     *
     * @return name of the ActionImport.
     */
    String name() default "";

    /**
     * If the return type of the action specified in the Action attribute is an entity or a collection of
     * entities, a SimpleIdentifier or TargetPath value MAY be specified for the EntitySet attribute that
     * names the entity set to which the returned entities belong. If a SimpleIdentifier is specified, it
     * MUST resolve to an entity set defined in the same entity container. If a TargetPath is specified,
     * it MUST resolve to an entity set in scope.
     *
     * If the return type is not an entity or a collection of entities, a value MUST NOT be defined for
     * the EntitySet attribute.
     */
    String entitySet() default "";

    /**
     * The edm:ActionImport element MUST include a QualifiedName value for the Action attribute which MUST
     * resolve to the name of an unbound edm:Action element in scope.
     */
    String action() default "";

    /**
     * A schema is identified by a Namespace. All edm:Schema elements MUST have a Namespace defined
     * through a Namespace attribute which MUST be unique within the document, and SHOULD be globally
     * unique. A schema cannot span more than one document.
     *
     *  @return name of the Namespace.
     */
    String namespace() default "";
}
