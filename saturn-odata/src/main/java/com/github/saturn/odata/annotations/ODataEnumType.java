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

import com.github.saturn.odata.enums.PrimitiveType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The edm:EnumType element represents an enumeration type in an entity model.
 *
 * The enumeration type element contains one or more child edm:Member elements defining the members of the
 * enumeration type.
 *
 * For more details:
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793947
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataEnumType {

    /**
     * The edm:EnumType element MUST include a Name attribute whose value is a SimpleIdentifier.
     * The value identifies the enumeration type and MUST be unique within its namespace.
     *
     * @return name of EnumType.
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
     * An enumeration type MAY include an UnderlyingType attribute to specify an underlying type whose value
     * MUST be one of Edm.Byte, Edm.SByte, Edm.Int16, Edm.Int32, or Edm.Int64. If the UnderlyingType attribute
     * is not specified, Edm.Int32 is used as the underlying type.
     *
     * @return UnderlyingType of the EnumType.
     */
    PrimitiveType underlyingType() default PrimitiveType.EDM_INT32;
}
