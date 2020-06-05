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

package com.github.sika.odata.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an entity set for an entity type. This annotation must only be used on classes that also have an
 * {@code EdmEntity} annotation.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ODataEntitySet {

    /**
     * The name of the entity set. If the name of the entity set is not specified using either this attribute or the
     * {@code name} attribute, the automatically pluralized name of the entity is used.
     *
     * @return The name of the entity set.
     */
    String name() default "";

    /**
     * The container that the entity set has. If not specified, the namespace of the entity set is used.
     * NOTE: The container of the first entity class will be used for the whole entity data model. In case where
     * other entity classes have container different than the first one, this container will not be taken into
     * account.
     *
     * @return container of the entity set.
     */
    String container() default "";

    /**
     * Specifies whether this entity set should be included in the service document.
     *
     * @return {@code true} if this entity set should be included in the service document, {@code false} otherwise.
     */
    boolean includedInServiceDocument() default true;

}
