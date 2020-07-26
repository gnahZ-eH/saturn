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
 * The edm:Parameter element allows one or more parameters to be passed to a function or action.
 *
 * For more details:
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793972
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ODataParameter {

    /**
     * The edm:Parameter element MUST include a Name attribute whose value is a SimpleIdentifier. The parameter
     * name MUST be unique within its parent element.
     *
     * @return name of the Parameter.
     */
    String name() default "";

    /**
     * The edm:Parameter element MUST include the Type attribute whose value is a TypeName indicating the type
     * of value that can be passed to the parameter.
     *
     * @return type of the Parameter.
     */
    String type() default "";

    /**
     * A parameter whose Type attribute does not specify a collection MAY specify a Boolean value for the
     * Nullable attribute. If not specified, the Nullable attribute defaults to true.
     *
     * The value of true means that the parameter accepts a null value.
     */
    boolean nullable() default true;

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
}
