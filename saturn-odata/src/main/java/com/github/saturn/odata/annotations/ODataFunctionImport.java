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
 * The edm:FunctionImport element allows exposing an unbound function as a top-level element in an
 * entity container.
 *
 * For more details:
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793998
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataFunctionImport {

    /**
     * The edm:FunctionImport element MUST include a Name attribute whose value is a SimpleIdentifier.
     * It MAY be identical to the last segment of the QualifiedName used to specify the Function attribute value.
     *
     * @return name of the FunctionImport.
     */
    String name() default "";

    /**
     * If the return type of the function specified in the Function attribute is an entity or a collection
     * of entities, a SimpleIdentifier or TargetPath value MAY be defined for the EntitySet attribute that
     * names the entity set to which the returned entities belong. If a SimpleIdentifier is specified, it
     * MUST resolve to an entity set defined in the same entity container. If a TargetPath is specified, it
     * MUST resolve to an entity set in scope.
     *
     * If the return type is not an entity or a collection of entities, a value MUST NOT be defined for
     * the EntitySet attribute.
     */
    String entitySet() default "";

    /**
     * The edm:FunctionImport element MUST include the Function attribute whose value MUST be a QualifiedName
     * that resolves to the name of an unbound edm:Function element in scope.
     */
    String function() default "";

    /**
     * A schema is identified by a Namespace. All edm:Schema elements MUST have a Namespace defined
     * through a Namespace attribute which MUST be unique within the document, and SHOULD be globally
     * unique. A schema cannot span more than one document.
     *
     *  @return name of the Namespace.
     */
    String namespace() default "";

    /**
     * The edm:FunctionImport for a parameterless function MAY include the IncludeInServiceDocument attribute
     * whose Boolean value indicates whether the function import is advertised in the service document.
     *
     * If no value is specified for this attribute, its value defaults to false.
     */
    boolean includeInServiceDocument() default false;
}
