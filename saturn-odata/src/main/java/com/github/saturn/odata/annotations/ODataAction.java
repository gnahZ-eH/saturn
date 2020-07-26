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
 * The edm:Action element represents an action in an entity model.
 *
 * Actions MAY have observable side effects and MAY return a single instance or a collection of instances
 * of any type. Actions cannot be composed with additional path segments.
 *
 * The action MAY specify a return type using the edm:ReturnType element. The return type must be a scalar,
 * entity or complex type, or a collection of scalar, entity or complex types.
 *
 * The action may also define zero or more edm:Parameter elements to be used during the execution of the action.
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793960
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataAction {

    /**
     * The edm:Action element MUST include a Name attribute whose value is a SimpleIdentifier.
     *
     * @return name of the Action.
     */
    String name() default "";

    /**
     * An action element MAY specify a Boolean value for the IsBound attribute.
     *
     * Actions whose IsBound attribute is false or not specified are considered unbound. Unbound actions are
     * invoked through an action import.
     *
     * Actions whose IsBound attribute is true are considered bound. Bound actions are invoked by appending a
     * segment containing the qualified action name to a segment of the appropriate binding parameter type
     * within the resource path. Bound actions MUST contain at least one edm:Parameter element, and the first
     * parameter is the binding parameter. The binding parameter can be of any type, and it MAY be nullable.
     */
    boolean isBound() default false;

    /**
     * Bound actions that return an entity or a collection of entities MAY specify a value for the EntitySetPath
     * attribute if determination of the entity set for the return type is contingent on the binding parameter.
     *
     * The value for the EntitySetPath attribute consists of a series of segments joined together with forward
     * slashes.
     *
     * The first segment of the entity set path MUST be the name of the binding parameter. The remaining segments
     * of the entity set path MUST represent navigation segments or type casts.
     *
     * A navigation segment names the SimpleIdentifier of the navigation property to be traversed. A type cast
     * segment names the QualifiedName of the entity type that should be returned from the type cast.
     */
    String entitySetPath() default "";

    /**
     * A schema is identified by a Namespace. All edm:Schema elements MUST have a Namespace defined
     * through a Namespace attribute which MUST be unique within the document, and SHOULD be globally
     * unique. A schema cannot span more than one document.
     *
     *  @return name of the Namespace.
     */
    String namespace() default "";
}

