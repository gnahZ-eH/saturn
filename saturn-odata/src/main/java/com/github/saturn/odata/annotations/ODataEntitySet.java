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
 * The edm:EntitySet element represents an entity set in an entity model.
 *
 * For more details:
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part3-csdl/odata-v4.0-os-part3-csdl.html#_Toc372793984
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataEntitySet {

    /**
     * The edm:EntitySet element MUST include a Name attribute whose value is a SimpleIdentifier.
     * @return name of the EntitySet.
     */
    String name() default "";

    /**
     * Each metadata document used to describe an OData service MUST define exactly one entity container.
     * Entity containers define the entity sets, singletons, function and action imports exposed by the service.
     *
     * An entity set allows access to entity type instances. Simple entity models frequently have one entity
     * set per entity type.
     *
     * @return container's name where the EntityType in.
     */
    String container() default "";

    /**
     * The edm:EntitySet element MAY include the IncludeInServiceDocument attribute whose Boolean value
     * indicates whether the entity set is advertised in the service document.
     *
     * Entity sets that cannot be queried without specifying additional query options SHOULD specify the
     * value false for this attribute.
     *
     * @return If no value is specified for this attribute, its value defaults to true.
     */
    boolean includedInServiceDocument() default true;
}
