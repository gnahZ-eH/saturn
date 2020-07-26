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
 * The edm:Property element defines a structural property.
 *
 * For more details:
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793906
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ODataProperty {

    /**
     * The edm:Property element MUST include a Name attribute whose value is a SimpleIdentifier used
     * when referencing, serializing or deserializing the property.
     *
     * The name of the structural property MUST be unique within the set of structural and navigation
     * properties of the containing structured type and any of its base types.
     *
     * @return name of the Property.
     */
    String name() default "";

    /**
     * The edm:Property element MUST include a Type attribute. The value of the Type attribute MUST be
     * the QualifiedName of a primitive type, complex type, or enumeration type in scope, or a collection
     * of one of these types.
     *
     * @return type of the Property.
     */
    String type() default "";

    /**
     * The edm:Property element MAY contain the Nullable attribute whose Boolean value specifies whether
     * a value is required for the property.
     *
     * If no value is specified for a property whose Type attribute does not specify a collection, the
     * Nullable attribute defaults to true.
     *
     * If the edm:Property element contains a Type attribute that specifies a collection, the property
     * MUST always exist, but the collection MAY be empty. In this case, the Nullable attribute applies
     * to members of the collection and specifies whether the collection can contain null values.
     */
    boolean nullable() default true;

    /**
     * A primitive or enumeration property MAY define a value for the DefaultValue attribute. The value
     * of this attribute determines the value of the property if the property is not explicitly represented
     * in an annotation or the body of a POST or PUT request.
     *
     * Default values of type Edm.String MUST be represented according to the XML escaping rules for character
     * data in attribute values. Values of other primitive types MUST be represented according to the appropriate
     * alternative in the primitiveValue rule defined in [OData-ABNF], i.e. Edm.Binary as binaryValue, Edm.Boolean
     * as booleanValue etc..
     *
     * If no value is specified, the DefaultValue attribute defaults to null.
     *
     * @return defaultValue of the Property.
     */
    String defaultValue() default "";

    /**
     * A binary, stream or string property MAY define a positive integer value for the MaxLength facet
     * attribute. The value of this attribute specifies the maximum length of the value of the property
     * on a type instance. Instead of an integer value the constant max MAY be specified as a shorthand
     * for the maximum length supported for the type by the service.
     *
     * If no value is specified, the property has unspecified length.
     *
     * @return max length of the Property.
     */
    int maxLength() default Integer.MAX_VALUE;

    /**
     * A datetime-with-offset, decimal, duration, or time-of-day property MAY define a value for the
     * Precision attribute.
     *
     * For a decimal property the value of this attribute specifies the maximum number of digits allowed
     * in the property’s value; it MUST be a positive integer. If no value is specified, the decimal property
     * has unspecified precision.
     *
     * For a temporal property the value of this attribute specifies the number of decimal places allowed
     * in the seconds portion of the property’s value; it MUST be a non-negative integer between zero and
     * twelve. If no value is specified, the temporal property has a precision of zero.
     *
     * @return precision of the Property.
     */
    int precision() default Integer.MAX_VALUE;

    /**
     * A decimal property MAY define a non-negative integer value or variable for the Scale attribute.
     * This attribute specifies the maximum number of digits allowed to the right of the decimal point.
     * The value variable means that the number of digits to the right of the decimal point may vary from
     * zero to the value of the Precision attribute.
     *
     * The value of the Scale attribute MUST be less than or equal to the value of the Precision attribute.
     *
     * If no value is specified, the Scale facet defaults to zero.
     *
     * @return scale of the Property.
     */
    int scale() default Integer.MAX_VALUE;

    /**
     * A geometry or geography property MAY define a value for the SRID attribute. The value of this attribute
     * identifies which spatial reference system is applied to values of the property on type instances.
     *
     * The value of the SRID attribute MUST be a non-negative integer or the special value variable.
     * If no value is specified, the attribute defaults to 0 for Geometry types or 4326 for Geography types.
     */
    int srid() default 0;

    /**
     * A string property MAY define a Boolean value for the Unicode attribute.
     *
     * A true value assigned to this attribute indicates that the value of the property is encoded with Unicode.
     * A false value assigned to this attribute indicates that the value of the property is encoded with ASCII.
     *
     * If no value is specified, the Unicode facet defaults to true.
     */
    boolean unicode() default true;
}
