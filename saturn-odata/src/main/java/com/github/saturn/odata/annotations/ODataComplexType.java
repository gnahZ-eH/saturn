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
 * Complex types are keyless nominal structured types. The lack of a key means that complex types cannot be
 * referenced, created, updated or deleted independently of an entity type. Complex types allow entity models
 * to group properties into common structures.
 *
 * A complex type can define two types of properties. A structural property is a named reference to a primitive,
 * complex, or enumeration type, or a collection of primitive, complex, or enumeration types. A navigation property
 * is a named reference to an entity type or a collection of entity types.
 *
 * All properties MUST have a unique name within a complex type. Properties MUST NOT have the same name as the
 * declaring complex type. They MAY have the same name as one of the direct or indirect base types or derived types.
 *
 * For more details:
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793941
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataComplexType {

    /**
     * The edm:ComplexType element MUST include a Name attribute whose value is a SimpleIdentifier.
     * The value identifies the complex type and MUST be unique within its namespace.
     *
     * @return name of the ComplexType.
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
     * A complex type MAY indicate that it is open by providing a value of true for the OpenType attribute.
     * An open type allows clients to add properties dynamically to instances of the type by specifying uniquely
     * named values in the payload used to insert or update an instance of the type.
     *
     * If not specified, the OpenType attribute defaults to false.
     *
     * A complex type derived from an open complex type MUST NOT provide a value of false for the OpenType attribute.
     *
     * @return true for open and false for not.
     */
    boolean openType() default false;
}
