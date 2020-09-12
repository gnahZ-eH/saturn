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

import com.github.saturn.odata.annotations.ODataEntityType;
import com.github.saturn.odata.annotations.ODataEntitySet;
import com.github.saturn.odata.annotations.ODataParameter;
import com.github.saturn.odata.annotations.ODataReturnType;
import com.github.saturn.odata.annotations.ODataFunction;
import com.github.saturn.odata.annotations.ODataNavigationProperty;
import com.github.saturn.odata.annotations.ODataProperty;
import com.github.saturn.odata.annotations.ODataEnumType;
import com.github.saturn.odata.annotations.ODataComplexType;
import com.github.saturn.odata.annotations.ODataAction;
import com.github.saturn.odata.exceptions.SaturnODataException;
import com.github.saturn.odata.metadata.SaturnEdmContext;
import com.github.saturn.odata.enums.PrimitiveType;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
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

public final class ODataUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ODataUtils.class);

    private ODataUtils() { }

    public static List<CsdlProperty> getCsdlProperties(final List<Field> fields, final String contextNamespace) {
        List<CsdlProperty> csdlProperties = new ArrayList<>();

        for (Field field : fields) {
            ODataProperty oDataProperty = field.getAnnotation(ODataProperty.class);

            if (oDataProperty != null) {
                Class<?> fieldType = field.getType();
                boolean collectionType = false;
                String propertyName = oDataProperty.name().trim().isEmpty() ? field.getName() : oDataProperty.name();
                FullQualifiedName propertyType = null;

                if (oDataProperty.type().isEmpty()) {
                    EdmPrimitiveTypeKind typeKind = getEdmPrimitiveType(fieldType);

                    if (typeKind != null) {
                        propertyType = typeKind.getFullQualifiedName();

                    } else if (Collection.class.isAssignableFrom(fieldType)) {
                        collectionType = true;
                        Type type = field.getGenericType();

                        if (type instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) type;
                            Class<?> argType = (Class<?>) (parameterizedType.getActualTypeArguments()[0]);
                            EdmPrimitiveTypeKind argTypeKind = getEdmPrimitiveType(argType);

                            if (argTypeKind != null) {
                                propertyType = argTypeKind.getFullQualifiedName();

                            } else if (argType.isAnnotationPresent(ODataComplexType.class)) {
                                ODataComplexType complexType = argType.getAnnotation(ODataComplexType.class);
                                propertyType = generateFQN(
                                        generateCollectionType(complexType.namespace(), complexType.name()));
                            }
                        }
                    } else if (fieldType.isAnnotationPresent(ODataComplexType.class)) {
                        ODataComplexType complexType = fieldType.getAnnotationsByType(ODataComplexType.class)[0];
                        String namespace = complexType.namespace().isEmpty() ? contextNamespace : complexType.namespace();
                        String name = complexType.name().isEmpty() ? fieldType.getSimpleName() : complexType.name();
                        propertyType = generateFQN(namespace, name);

                    } else if (fieldType.isAnnotationPresent(ODataEnumType.class)) {
                        ODataEnumType oDataEnumType = fieldType.getAnnotation(ODataEnumType.class);
                        String namespace = oDataEnumType.namespace().isEmpty() ? contextNamespace : oDataEnumType.namespace();
                        String name = oDataEnumType.name().isEmpty() ? fieldType.getSimpleName() : oDataEnumType.name();
                        propertyType = generateFQN(namespace, name);
                    }
                } else {
                    propertyType = getEdmPrimitiveType(oDataProperty.type()).getFullQualifiedName();
                }

                LOG.debug("Read Property: {} -> {}", propertyName, propertyType);

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

    public static List<CsdlNavigationProperty> getCsdlNavigationProperties(final List<Field> fields, final String contextNamespace) throws SaturnODataException {
        List<CsdlNavigationProperty> csdlNavigationProperties = new ArrayList<>();

        for (Field field : fields) {
            ODataNavigationProperty oDataNavigationProperty = field.getAnnotation(ODataNavigationProperty.class);

            if (oDataNavigationProperty != null) {
                String propertyName = oDataNavigationProperty.name().isEmpty() ? field.getName() : oDataNavigationProperty.name();
                String propertyTypeName = oDataNavigationProperty.type().isEmpty() ? null : oDataNavigationProperty.type();
                boolean collectionType = false;

                if (propertyTypeName == null) {
                    Class<?> fieldType = field.getType();

                    if (Collection.class.isAssignableFrom(fieldType)) {
                        collectionType = true;
                        Type type = field.getGenericType();

                        if (type instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) type;
                            Class<?> argType = (Class<?>) (parameterizedType.getActualTypeArguments()[0]);
                            ODataEntityType oDataEntityType = argType.getAnnotation(ODataEntityType.class);
                            ExceptionUtils.assertNotNull(oDataEntityType, field);
                            propertyTypeName = oDataEntityType.name();
                        }
                    } else {
                        ODataEntityType oDataEntityType = fieldType.getAnnotation(ODataEntityType.class);
                        ExceptionUtils.assertNotNull(oDataEntityType, field);
                        propertyTypeName = oDataEntityType.name();
                    }
                }

                LOG.debug("Read NavigationProperty: {} -> {}", propertyName, propertyTypeName);

                CsdlNavigationProperty csdlNavigationProperty = new CsdlNavigationProperty()
                        .setName(propertyName)
                        .setType(generateFQN(contextNamespace, propertyTypeName))
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

    public static List<CsdlNavigationPropertyBinding> getCsdlNavigationPropertyBindings(final List<Field> fields) throws SaturnODataException {
        List<CsdlNavigationPropertyBinding> csdlNavigationPropertyBindings = new ArrayList<>();

        for (Field field : fields) {
            ODataNavigationProperty oDataNavigationProperty = field.getAnnotation(ODataNavigationProperty.class);

            if (oDataNavigationProperty != null) {
                String propertyName = oDataNavigationProperty.name().isEmpty() ? field.getName() : oDataNavigationProperty.name();
                String propertyTypeName = oDataNavigationProperty.type().isEmpty() ? null : oDataNavigationProperty.type();

                if (propertyTypeName == null) {
                    Class<?> fieldType = field.getType();

                    if (Collection.class.isAssignableFrom(fieldType)) {
                        Type type = field.getGenericType();

                        if (type instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) type;
                            Class<?> argType = (Class<?>) (parameterizedType.getActualTypeArguments()[0]);
                            ODataEntitySet oDataEntitySet = argType.getAnnotation(ODataEntitySet.class);
                            ExceptionUtils.assertNotNull(oDataEntitySet, field);
                            propertyTypeName = oDataEntitySet.name();
                        }
                    } else {
                        ODataEntitySet oDataEntitySet = fieldType.getAnnotation(ODataEntitySet.class);
                        ExceptionUtils.assertNotNull(oDataEntitySet, field);
                        propertyTypeName = oDataEntitySet.name();
                    }
                }

                LOG.debug("Read NavigationPropertyBinding: {} -> {}", propertyName, propertyTypeName);

                CsdlNavigationPropertyBinding csdlNavigationPropertyBinding = new CsdlNavigationPropertyBinding()
                        .setPath(propertyName)
                        .setTarget(propertyTypeName);
                csdlNavigationPropertyBindings.add(csdlNavigationPropertyBinding);
            }
        }
        return csdlNavigationPropertyBindings;
    }

    public static CsdlFunction getFunction(final FullQualifiedName fullQualifiedName, final SaturnEdmContext context) {
        String functionName = fullQualifiedName.getName();
        Class<?> clazz = context.getFunctions().get(functionName);
        if (clazz == null) {
            return null;
        }

        ODataFunction oDataFunction = clazz.getAnnotation(ODataFunction.class);
        ODataReturnType oDataReturnType = clazz.getAnnotation(ODataReturnType.class);
        CsdlFunction csdlFunction = new CsdlFunction()
                .setName(oDataFunction.name())
                .setBound(oDataFunction.isBound());

        if (oDataReturnType != null) {
            CsdlReturnType csdlReturnType = generateCsdlReturnType(oDataReturnType, context.getNameSpace());
            csdlFunction.setReturnType(csdlReturnType);
        }

        if (oDataFunction.isBound()) {
            csdlFunction.setEntitySetPath(oDataFunction.entitySetPath());
            Class<?> entitySetPath = context.getEntitySets().get(oDataFunction.entitySetPath());
            ODataEntityType oDataEntityType = entitySetPath.getAnnotation(ODataEntityType.class);
            CsdlParameter csdlParameter = new CsdlParameter()
                    .setName(oDataFunction.entitySetPath())
                    .setType(generateFQN(oDataEntityType.namespace(), oDataEntityType.name()));
            csdlFunction.getParameters().add(csdlParameter);
        }

        for (Field field : clazz.getDeclaredFields()) {
            ODataParameter oDataParameter = field.getAnnotation(ODataParameter.class);

            if (oDataParameter != null) {
                Class<?> fieldType = field.getType();
                String oDataParameterName = oDataParameter.name().trim().isEmpty()
                        ? field.getName() : oDataParameter.name();
                FullQualifiedName oDataParameterType = null;

                if (oDataParameter.type().isEmpty()) {
                    EdmPrimitiveTypeKind typeKind = getEdmPrimitiveType(fieldType);

                    if (typeKind != null) {
                        oDataParameterType = typeKind.getFullQualifiedName();
                    } else {

                        if (fieldType.isAnnotationPresent(ODataEnumType.class)) {
                            ODataEnumType oDataEnumType = fieldType.getAnnotation(ODataEnumType.class);
                            String namespace = oDataEnumType.namespace().isEmpty() ? context.getNameSpace() : oDataEnumType.namespace();
                            String name = oDataEnumType.name().isEmpty() ? fieldType.getSimpleName() : oDataEnumType.name();
                            oDataParameterType = generateFQN(namespace, name);
                        }
                    }
                } else {
                    EdmPrimitiveTypeKind typeKind = getEdmPrimitiveType(oDataParameter.type());

                    if (typeKind != null) {
                        oDataParameterType = typeKind.getFullQualifiedName();
                    } else {
                        oDataParameterType = generateFQN(context.getNameSpace(), oDataParameter.type());
                    }
                }

                CsdlParameter csdlParameter = new CsdlParameter()
                        .setName(oDataParameterName)
                        .setType(oDataParameterType)
                        .setNullable(oDataParameter.nullable());
                csdlFunction.getParameters().add(csdlParameter);
            }
        }
        return csdlFunction;
    }


    public static CsdlAction getAction(final FullQualifiedName fullQualifiedName, final SaturnEdmContext context) {
        String actionName = fullQualifiedName.getName();
        Class<?> clazz = context.getActions().get(actionName);
        if (clazz == null) {
            return null;
        }

        ODataAction oDataAction = clazz.getAnnotation(ODataAction.class);
        ODataReturnType oDataReturnType = clazz.getAnnotation(ODataReturnType.class);
        CsdlAction csdlAction = new CsdlAction()
                .setName(oDataAction.name())
                .setBound(oDataAction.isBound());

        if (oDataReturnType != null) {
            CsdlReturnType csdlReturnType = generateCsdlReturnType(oDataReturnType, context.getNameSpace());
            csdlAction.setReturnType(csdlReturnType);
        }

        if (oDataAction.isBound()) {
            csdlAction.setEntitySetPath(oDataAction.entitySetPath());
            Class<?> entitySetPath = context.getEntitySets().get(oDataAction.entitySetPath());
            ODataEntityType oDataEntityType = entitySetPath.getAnnotation(ODataEntityType.class);
            CsdlParameter csdlParameter = new CsdlParameter()
                    .setName(oDataAction.entitySetPath())
                    .setType(generateFQN(oDataEntityType.namespace(), oDataEntityType.name()));
            csdlAction.getParameters().add(csdlParameter);
        }

        for (Field field : clazz.getDeclaredFields()) {
            ODataParameter oDataParameter = field.getAnnotation(ODataParameter.class);

            if (oDataParameter != null) {
                Class<?> fieldType = field.getType();
                String oDataParameterName = oDataParameter.name().trim().isEmpty()
                        ? field.getName() : oDataParameter.name();
                FullQualifiedName oDataParameterType = null;
                boolean collectionType = false;

                if (oDataParameter.type().isEmpty()) {
                    EdmPrimitiveTypeKind typeKind = getEdmPrimitiveType(fieldType);

                    if (typeKind != null) {
                        oDataParameterType = typeKind.getFullQualifiedName();
                    } else if (fieldType.isAssignableFrom(List.class)) {
                        collectionType = true;
                        Type type = field.getGenericType();

                        if (type instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) type;
                            Class<?> argType = (Class<?>) (parameterizedType.getActualTypeArguments()[0]);
                            EdmPrimitiveTypeKind argTypeKind = getEdmPrimitiveType(argType);

                            if (argTypeKind != null) {
                                oDataParameterType = argTypeKind.getFullQualifiedName();
                            } else {
                                oDataParameterType = getFullQualifiedNameFromClassType(argType, context.getNameSpace());
                            }
                        }
                    } else {
                        oDataParameterType = getFullQualifiedNameFromClassType(fieldType, context.getNameSpace());
                    }
                } else {
                    oDataParameterType = getEdmPrimitiveType(oDataParameter.type()).getFullQualifiedName();
                }

                CsdlParameter csdlParameter = new CsdlParameter()
                        .setName(oDataParameterName)
                        .setType(oDataParameterType)
                        .setCollection(collectionType)
                        .setNullable(oDataParameter.nullable());

                if (oDataParameterType != null
                        && oDataParameterType.equals(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName())) {
                    csdlParameter.setPrecision(oDataParameter.precision());
                    csdlParameter.setScale(oDataParameter.scale());
                }

                csdlAction.getParameters().add(csdlParameter);
            }
        }
        return csdlAction;
    }

    public static CsdlAction getAction(final String namespace, final String name, final SaturnEdmContext context) {
        return getAction(generateFQN(namespace, name), context);
    }

    public static CsdlFunction getFunction(final String namespace, final String name, final SaturnEdmContext context) {
        return getFunction(generateFQN(namespace, name), context);
    }

    public static EdmPrimitiveTypeKind getEdmPrimitiveType(final String type) {
        EdmPrimitiveTypeKind edmPrimitiveTypeKind;
        edmPrimitiveTypeKind = PrimitiveType.EDM_PT_BY_NAME.get(type);
        return edmPrimitiveTypeKind;
    }

    public static EdmPrimitiveTypeKind getEdmPrimitiveType(final Class<?> type) {
        if (type.isAssignableFrom(Integer.class)
                || type.isAssignableFrom(int.class)) {
            return EdmPrimitiveTypeKind.Int32;
        } else if (type.isAssignableFrom(Long.class)
                || type.isAssignableFrom(long.class)) {
            return EdmPrimitiveTypeKind.Int64;
        } else if (type.isAssignableFrom(Float.class)
                || type.isAssignableFrom(float.class)
                || type.isAssignableFrom(Double.class)
                || type.isAssignableFrom(double.class)) {
            return EdmPrimitiveTypeKind.Double;
        } else if (type.isAssignableFrom(String.class)) {
            return EdmPrimitiveTypeKind.String;
        } else if (type.isAssignableFrom(Boolean.class)
                || type.isAssignableFrom(boolean.class)) {
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

    public static FullQualifiedName getFullQualifiedNameFromClassType(final Class<?> clazz, final String contextNamespace) {
        FullQualifiedName fullQualifiedName = null;

        if (clazz.isAnnotationPresent(ODataEnumType.class)) {
            ODataEnumType oDataEnumType = clazz.getAnnotation(ODataEnumType.class);
            String namespace = oDataEnumType.namespace().isEmpty() ? contextNamespace : oDataEnumType.namespace();
            String name = oDataEnumType.name().isEmpty() ? clazz.getSimpleName() : oDataEnumType.name();
            fullQualifiedName = generateFQN(namespace, name);
        }

        if (clazz.isAnnotationPresent(ODataEntityType.class)) {
            ODataEntityType oDataEntityType = clazz.getAnnotation(ODataEntityType.class);
            String namespace = oDataEntityType.namespace().isEmpty() ? contextNamespace : oDataEntityType.namespace();
            String name = oDataEntityType.name().isEmpty() ? clazz.getSimpleName() : oDataEntityType.name();
            fullQualifiedName = generateFQN(namespace, name);
        }

        if (clazz.isAnnotationPresent(ODataComplexType.class)) {
            ODataComplexType oDataComplexType = clazz.getAnnotation(ODataComplexType.class);
            String namespace = oDataComplexType.namespace().isEmpty() ? contextNamespace : oDataComplexType.namespace();
            String name = oDataComplexType.name().isEmpty() ? clazz.getSimpleName() : oDataComplexType.name();
            fullQualifiedName = generateFQN(namespace, name);
        }

        return fullQualifiedName;
    }

    public static CsdlReturnType generateCsdlReturnType(final ODataReturnType oDataReturnType, final String namespace) {
        boolean isCollectionType = oDataReturnType.type().startsWith(StringUtils.COLLECTION);
        CsdlReturnType csdlReturnType = new CsdlReturnType()
                .setNullable(oDataReturnType.nullable())
                .setScale(oDataReturnType.scale())
                .setCollection(isCollectionType)
                .setPrecision(oDataReturnType.precision());

        if (isCollectionType) {
            csdlReturnType.setType(
                    generateCollectionType(namespace, getTypeStringFromCollection(oDataReturnType.type())));
        } else {
            csdlReturnType.setType(generateFQN(namespace, oDataReturnType.type()));
        }
        return csdlReturnType;
    }

    public static FullQualifiedName generateFQN(final String namespace, final String name) {
        String typeName = getTypeStringFromCollection(name);
        return new FullQualifiedName(namespace, typeName);
    }

    public static String getTypeStringFromCollection(final String s) {
        if (StringUtils.isNotNull(s) && s.startsWith(StringUtils.COLLECTION)) {
            return s.substring(s.indexOf('(') + 1, s.length() - 1);
        }
        return s;
    }

    public static FullQualifiedName generateFQN(final String namespaceAndName) {
        return new FullQualifiedName(namespaceAndName);
    }

    public static String generateCollectionType(final String namespace, final String typeName) {
        return String.format(StringUtils.COLLECTION_QUALIFIED_NAME, namespace, typeName);
    }

    public static boolean isNull(final FullQualifiedName fullQualifiedName) {
        return fullQualifiedName == null;
    }
}
