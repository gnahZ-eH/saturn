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
 * The edm:Function element represents a function in an entity model.
 *
 * Functions MUST NOT have observable side effects and MUST return a single instance or a collection of
 * instances of any type. Functions MAY be composable.
 *
 * The function MUST specify a return type using the edm:ReturnType element. The return type must be a
 * scalar, entity or complex type, or a collection of scalar, entity or complex types.
 *
 * The function may also define zero or more edm:Parameter elements to be used during the execution of
 * the function.
 *
 * For more details:
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793966
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataFunction {

    /**
     * The edm:Function element MUST include a Name attribute whose value is a SimpleIdentifier.
     * @return name of the Function.
     */
    String name() default "";

    /**
     * An action or function overload MAY indicate that it is bound. If not explicitly indicated, it is unbound.
     *
     * Bound actions or functions are invoked on resources matching the type of the binding parameter.
     * The binding parameter can be of any type, and it MAY be Nullable.
     *
     * Unbound actions are invoked through an action import.
     *
     * Unbound functions are invoked as static functions within a filter or orderby expression, or from the
     * entity container through a function import.
     *
     * @return The value of IsBound is one of the Boolean literals true or false.
     */
    boolean isBound() default false;

    /**
     * Bound functions that return an entity or a collection of entities MAY specify a value for the
     * EntitySetPath attribute if determination of the entity set for the return type is contingent on
     * the binding parameter.
     *
     * The value for the EntitySetPath attribute consists of a series of segments joined together with
     * forward slashes.
     *
     * The first segment of the entity set path MUST be the name of the binding parameter. The remaining
     * segments of the entity set path MUST represent navigation segments or type casts.
     */
    String entitySetPath() default "";

    /**
     * A function element MAY specify a Boolean value for the IsComposable attribute. If no value is specified
     * for the IsComposable attribute, the value defaults to false.
     *
     * Functions whose IsComposable attribute is true are considered composable. A composable function can be
     * invoked with additional path segments or system query options appended to the path that identifies the
     * composable function as appropriate for the type returned by the composable function.
     */
    boolean isComposable() default false;

    /**
     * A schema is identified by a Namespace. All edm:Schema elements MUST have a Namespace defined
     * through a Namespace attribute which MUST be unique within the document, and SHOULD be globally
     * unique. A schema cannot span more than one document.
     *
     *  @return name of the Namespace.
     */
    String namespace() default "";
}
