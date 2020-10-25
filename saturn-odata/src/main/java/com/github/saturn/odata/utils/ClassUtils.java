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

package com.github.saturn.odata.utils;

import com.github.saturn.odata.annotations.ODataNavigationProperty;
import com.github.saturn.odata.annotations.ODataProperty;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ClassUtils {

    private ClassUtils() { }

    public static List<Field> getFields(final Class<?> clazz) {
        return getFields(clazz, true);
    }

    public static List<Field> getFields(final Class<?> clazz, final boolean superClass) {
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        if (superClass) {
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz != null) {
                fields.addAll(Arrays.asList(superClazz.getDeclaredFields()));
            }
        }
        return fields;
    }

    public static Field getFieldFromEdmClass(Class<?> edmClass, String propertyName) {
        List<Field> fields = getFields(edmClass);
        for (Field field : fields) {
            ODataProperty oDataProperty = field.getAnnotation(ODataProperty.class);
            ODataNavigationProperty oDataNavigationProperty = field.getAnnotation(ODataNavigationProperty.class);
            if ((oDataProperty == null && field.getName().equals(propertyName))
                    || (oDataProperty != null && !oDataProperty.name().isEmpty() && oDataProperty.name().equals(propertyName))
                    || (oDataProperty != null && oDataProperty.name().isEmpty() && field.getName().equals(propertyName))
                    || (oDataNavigationProperty == null && field.getName().equals(propertyName))
                    || (oDataNavigationProperty != null && !oDataNavigationProperty.name().isEmpty() && oDataNavigationProperty.name().equals(propertyName))
                    || (oDataNavigationProperty != null && oDataNavigationProperty.name().isEmpty() && field.getName().equals(propertyName))) {
                return field;
            }
        }
        return null;
    }

    public static ClassPathScanningCandidateComponentProvider createComponentScanner(final Iterable<Class<? extends Annotation>> annotations) {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        for (Class<? extends Annotation> annotation : annotations) {
            provider.addIncludeFilter(new AnnotationTypeFilter(annotation));
        }
        return provider;
    }
}
