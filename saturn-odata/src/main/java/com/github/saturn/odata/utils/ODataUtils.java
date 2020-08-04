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

import com.github.saturn.odata.annotations.*;
import com.github.saturn.odata.metadata.SaturnEdmContext;
import com.github.saturn.odata.enums.PrimitiveType;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ODataUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ODataUtils.class);

    public static List<CsdlProperty> getCsdlProperties(List<Field> fields, SaturnEdmContext context) {
        List<CsdlProperty> csdlProperties = new ArrayList<>();

        for (Field field : fields) {
            ODataProperty oDataProperty = field.getAnnotation(ODataProperty.class);

            if (oDataProperty != null) {
                Class<?> fieldType = field.getType();
                boolean collectionType = false;
                boolean cpxType = fieldType.getAnnotationsByType(ODataComplexType.class).length != 0;
                String propertyName = oDataProperty.name().trim().isEmpty() ? field.getName() : oDataProperty.name();
                FullQualifiedName propertyType = null;

                if (oDataProperty.type().isEmpty()) {
                    EdmPrimitiveTypeKind typeKind = getEdmPrimitiveType(fieldType);

                    if (typeKind != null) {
                        propertyType = typeKind.getFullQualifiedName();
                    } else if (fieldType.isAssignableFrom(Collection.class)) {
                        collectionType = true;
                        Type type = field.getGenericType();

                        if (type instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) type;
                            Class<?> argType = (Class<?>) (parameterizedType.getActualTypeArguments()[0]);
                            EdmPrimitiveTypeKind argTypeKind = getEdmPrimitiveType(argType);

                            if (argTypeKind != null) {
                                propertyType = argTypeKind.getFullQualifiedName();
                            }

                            if (argType.isAnnotationPresent(ODataComplexType.class)) {
                                ODataComplexType complexType = argType.getAnnotation(ODataComplexType.class);
                                propertyType = new FullQualifiedName(
                                        String.format(StringUtils.COLLECTION_QUALIFIED_NAME, complexType.namespace(), complexType.name()));
                            }
                        }
                    } else if (cpxType) {
                        ODataComplexType complexType = fieldType.getAnnotationsByType(ODataComplexType.class)[0];
                        String namespace = complexType.namespace().isEmpty() ? context.getNameSpace() : complexType.namespace();
                        String name = complexType.name().isEmpty() ? fieldType.getSimpleName() : complexType.name();
                        propertyType = generateFQN(namespace, name);
                    } else {
                        ODataEnumType oDataEnumType = fieldType.getAnnotation(ODataEnumType.class);

                        if (oDataEnumType != null) {
                            String namespace = oDataEnumType.namespace().isEmpty() ? context.getNameSpace() : oDataEnumType.namespace();
                            String name = oDataEnumType.name().isEmpty() ? fieldType.getSimpleName() : oDataEnumType.name();
                            propertyType = generateFQN(namespace, name);
                        }
                    }
                } else {
                    propertyType = getEdmPrimitiveType(oDataProperty.type()).getFullQualifiedName();
                }

                LOG.debug("Load property: {}-{}", propertyName, propertyType);

                CsdlProperty csdlProperty = new CsdlProperty()
                        .setName(propertyName)
                        .setType(propertyType)
                        .setCollection(collectionType)
                        .setNullable(oDataProperty.nullable());

                csdlProperties.add(csdlProperty);
            }
        }
        return csdlProperties;
    }

    public static EdmPrimitiveTypeKind getEdmPrimitiveType(Class<?> type) {
        if (type.isAssignableFrom(Integer.class)) {
            return EdmPrimitiveTypeKind.Int32;
        } else if (type.isAssignableFrom(Long.class)) {
            return EdmPrimitiveTypeKind.Int64;
        } else if (type.isAssignableFrom(Float.class) ||
                   type.isAssignableFrom(Double.class)) {
            return EdmPrimitiveTypeKind.Double;
        } else if (type.isAssignableFrom(String.class)) {
            return EdmPrimitiveTypeKind.String;
        } else if (type.isAssignableFrom(Boolean.class)) {
            return EdmPrimitiveTypeKind.Boolean;
        } else if (type.isAssignableFrom(LocalDate.class)) {
            return EdmPrimitiveTypeKind.Date;
        } else if (type.isAssignableFrom(LocalDateTime.class)) {
            return EdmPrimitiveTypeKind.DateTimeOffset;
        } else if (type.isAssignableFrom(BigDecimal.class)) {
            return EdmPrimitiveTypeKind.Decimal;
        }
        return null;
    }

    public static EdmPrimitiveTypeKind getEdmPrimitiveType(String type) {
        EdmPrimitiveTypeKind edmPrimitiveTypeKind;
        edmPrimitiveTypeKind = PrimitiveType.EDM_PT_BY_NAME.get(type);
        return edmPrimitiveTypeKind;
    }

    public static FullQualifiedName generateFQN(String namespace, String name) {
        return new FullQualifiedName(namespace, name);
    }

    public static List<CsdlNavigationProperty> getCsdlNavigationProperties(List<Field> fields, SaturnEdmContext context) {
        List<CsdlNavigationProperty> csdlNavigationProperties = new ArrayList<>();

        for (Field field : fields) {
            ODataNavigationProperty oDataNavigationProperty = field.getAnnotation(ODataNavigationProperty.class);

            if (oDataNavigationProperty != null) {
                String propertyName = oDataNavigationProperty.name().isEmpty() ? field.getName() : oDataNavigationProperty.name();
                String propertyTypeName = oDataNavigationProperty.type().isEmpty() ? null : oDataNavigationProperty.type();
                boolean collectionType = false;

                if (propertyTypeName == null) {
                    Class<?> fieldType = field.getType();

                    if (fieldType.isAssignableFrom(Collection.class)) {
                        collectionType = true;
                        Type type = field.getGenericType();

                        if (type instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) type;
                            Class<?> argType = (Class<?>) (parameterizedType.getActualTypeArguments()[0]);
                            ODataEntityType oDataEntityType = argType.getAnnotation(ODataEntityType.class);
                            propertyTypeName = oDataEntityType == null ? null : oDataEntityType.name();
                        }
                    } else {
                        ODataEntityType oDataEntityType = fieldType.getAnnotation(ODataEntityType.class);
                        propertyTypeName = oDataEntityType == null ? null : oDataEntityType.name();
                    }
                }

                CsdlNavigationProperty csdlNavigationProperty = new CsdlNavigationProperty()
                        .setName(propertyName)
                        .setType(generateFQN(context.getNameSpace(), propertyTypeName))
                        .setCollection(collectionType)
                        .setNullable(oDataNavigationProperty.nullable());

                if (!oDataNavigationProperty.partner().isEmpty()) {
                    csdlNavigationProperty.setPartner(oDataNavigationProperty.partner());
                }

                csdlNavigationProperties.add(csdlNavigationProperty);
            }
        }
        return csdlNavigationProperties;
    }
}
