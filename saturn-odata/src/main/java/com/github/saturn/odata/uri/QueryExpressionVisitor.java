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

package com.github.saturn.odata.uri;

import com.github.saturn.odata.annotations.ODataEntityType;
import com.github.saturn.odata.annotations.ODataNavigationProperty;
import com.github.saturn.odata.annotations.ODataProperty;
import com.github.saturn.odata.exceptions.SaturnODataException;
import com.github.saturn.odata.utils.ClassUtils;
import com.github.saturn.odata.utils.ExceptionUtils;
import com.github.saturn.odata.utils.StringUtils;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathBuilder;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourcePrimitiveProperty;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriResourceComplexProperty;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class QueryExpressionVisitor extends AbstractExpressionVisitor {

    private static final Logger LOG = LoggerFactory.getLogger(QueryExpressionVisitor.class);

    // JPA Entity and OData Edm are combined
    private Class<?> oDataEntityClass;

    public QueryExpressionVisitor(Class<?> oDataEntityClass) {
        this.oDataEntityClass = oDataEntityClass;
    }

    @Override
    public Expression<?> visitMember(Member member) throws ExpressionVisitException, ODataApplicationException {

        UriInfoResource uriInfoResource = member.getResourcePath();
        ODataEntityType oDataEntityType = oDataEntityClass.getAnnotation(ODataEntityType.class);
        Class<?> jpaEntityClass = oDataEntityType.jpaEntity();
        PathBuilder<?> pathBuilder = new PathBuilder<>(jpaEntityClass, oDataEntityType.jpaVariable());

        for (UriResource uriResource : uriInfoResource.getUriResourceParts()) {

            switch (uriResource.getKind()) {
                case primitiveProperty: {
                    UriResourcePrimitiveProperty primitiveProperty = (UriResourcePrimitiveProperty) uriResource;
                    String propertyName = primitiveProperty.getProperty().getName();
                    Field field = ClassUtils.getFieldFromEdmClass(oDataEntityClass, propertyName);
                    String jpaEntityFieldNameWithPath = null;
                    // this part should be tested carefully.
                    // todo
                    try {
                        ExceptionUtils.assertNotNull(field, Field.class.getSimpleName(), propertyName);
                        ODataProperty oDataProperty = field.getAnnotation(ODataProperty.class);

                        if (oDataProperty == null) {
                            jpaEntityFieldNameWithPath = field.getName();
                        } else {
                            jpaEntityFieldNameWithPath = oDataProperty.jpaVariable().trim().isEmpty() ? field.getName() : oDataProperty.jpaVariable();
                        }

                        jpaEntityFieldNameWithPath = oDataEntityType.superEntityName() + StringUtils.POINT + jpaEntityFieldNameWithPath;

                        if (primitiveProperty.getProperty().getType().getKind().equals(EdmTypeKind.ENUM)) {
                            oDataEntityClass = field.getType();
                        }
                    } catch (SaturnODataException e) {
                        // just for assert part
                        LOG.error(e.getMessage());
                    }
                    return getJPAEntityPath(jpaEntityClass, pathBuilder, jpaEntityFieldNameWithPath);
                }
                case navigationProperty: {
                    UriResourceNavigation navigation = (UriResourceNavigation) uriResource;
                    String propertyName = navigation.getProperty().getName();
                    Field field = ClassUtils.getFieldFromEdmClass(oDataEntityClass, propertyName);
                    String jpaEntityFieldNameWithPath = null;
                    // this part should be tested carefully.
                    // todo
                    try {
                        ExceptionUtils.assertNotNull(field, Field.class.getSimpleName(), propertyName);
                        ODataNavigationProperty oDataNavigationProperty = field.getAnnotation(ODataNavigationProperty.class);

                        if (oDataNavigationProperty == null) {
                            jpaEntityFieldNameWithPath = field.getName();
                        } else {
                            jpaEntityFieldNameWithPath = oDataNavigationProperty.jpaVariable().trim().isEmpty() ? field.getName() : oDataNavigationProperty.jpaVariable();
                        }

                        jpaEntityFieldNameWithPath = oDataEntityType.superEntityName() + StringUtils.POINT + jpaEntityFieldNameWithPath;
                        pathBuilder = (PathBuilder<?>) getJPAEntityPath(jpaEntityClass, pathBuilder, jpaEntityFieldNameWithPath);
                        oDataEntityClass = field.getType();
                        ODataEntityType naviEntityType = oDataEntityClass.getAnnotation(ODataEntityType.class);

                        if (oDataNavigationProperty != null && oDataNavigationProperty.jpaEntity().equals(Object.class)) {
                            jpaEntityClass = oDataNavigationProperty.jpaEntity();
                        } else {
                            jpaEntityClass = naviEntityType.jpaEntity();
                        }
                    } catch (SaturnODataException e) {
                        // just for assert part
                        LOG.error(e.getMessage());
                    }
                    break;
                }
                case complexProperty: {
                    UriResourceComplexProperty complexProperty = (UriResourceComplexProperty) uriResource;
                    String propertyName = complexProperty.getProperty().getName();
                    Field field = ClassUtils.getFieldFromEdmClass(oDataEntityClass, propertyName);
                    String jpaEntityFieldNameWithPath = null;

                    try {
                        ExceptionUtils.assertNotNull(field, Field.class.getSimpleName(), propertyName);
                        ODataProperty oDataProperty = field.getAnnotation(ODataProperty.class);
                        // todo
                        // need to check here
                        ODataEntityType complexEntityType = oDataEntityType;

                        if (!field.getDeclaringClass().equals(oDataEntityClass)) {
                            pathBuilder = (PathBuilder<?>) getJPAEntityPath(jpaEntityClass, pathBuilder, oDataEntityType.superEntityName());
                            oDataEntityClass = field.getDeclaringClass();
                            complexEntityType = oDataEntityClass.getAnnotation(ODataEntityType.class);
                            if (complexEntityType != null) {
                                jpaEntityClass = complexEntityType.jpaEntity();
                            }
                        }
                        // todo
                        // need to test here carefully
                        if (oDataProperty != null) {
                            jpaEntityFieldNameWithPath = oDataProperty.jpaVariable().trim().isEmpty() ? field.getName() : oDataProperty.jpaVariable();
                            if (!oDataProperty.jpaEntity().equals(Object.class)) {
                                pathBuilder = (PathBuilder<?>) getJPAEntityPath(jpaEntityClass, pathBuilder, jpaEntityFieldNameWithPath);
                                jpaEntityClass = oDataProperty.jpaEntity();
                            } else {
                                if (complexEntityType != null && !complexEntityType.superEntityName().trim().isEmpty()) {
                                    jpaEntityFieldNameWithPath = complexEntityType.superEntityName() + StringUtils.POINT + jpaEntityFieldNameWithPath;
                                }
                                pathBuilder = (PathBuilder<?>) getJPAEntityPath(jpaEntityClass, pathBuilder, jpaEntityFieldNameWithPath);
                                jpaEntityClass = complexEntityType.jpaEntity();
                            }
                        }
                        oDataEntityClass = field.getType();
                    } catch (SaturnODataException e) {
                        // just for assert part
                        LOG.error(e.getMessage());
                    }
                    break;
                }
                case entitySet:
                default:
                    break;
            }
        }
        return pathBuilder;
    }

    @SuppressWarnings("unchecked")
    private Expression<?> getJPAEntityPath(Class<?> jpaEntity, Path<?> path, String jpaEntityFieldNameWithPath) {
        String[] paths = jpaEntityFieldNameWithPath.split(StringUtils.REGEX_POINT);
        Class<?> entity = jpaEntity;
        PathBuilder<?> pathBuilder = (PathBuilder<?>) path;

        for (String fieldName : paths) {
            List<Field> fields = ClassUtils.getFields(entity);
            for (Field field : fields) {
                if (field.getName().equals(fieldName)) {
                    entity = field.getType();
                    if (entity.isAssignableFrom(Integer.class) || entity.isAssignableFrom(int.class)) {
                        return pathBuilder.getNumber(fieldName, (Class<Integer>) entity);
                    } else if (entity.isAssignableFrom(Long.class) || entity.isAssignableFrom(long.class)) {
                        return pathBuilder.getNumber(fieldName, (Class<Long>) entity);
                    } else if (entity.isAssignableFrom(String.class)) {
                        return pathBuilder.getString(fieldName);
                    } else if (entity.isAssignableFrom(Boolean.class) || entity.isAssignableFrom(boolean.class)) {
                        return pathBuilder.getBoolean(fieldName);
                    } else if (entity.isAssignableFrom(Date.class)) {
                        return pathBuilder.getDate(fieldName, (Class<Date>) entity);
                    } else if (entity.isAssignableFrom(LocalDate.class)) {
                        return pathBuilder.getDate(fieldName, (Class<LocalDate>) entity);
                    } else if (entity.isAssignableFrom(LocalDateTime.class)) {
                        return pathBuilder.getDate(fieldName, (Class<LocalDateTime>) entity);
                    } else if (entity.isAnnotationPresent(Entity.class)) {
                        pathBuilder = pathBuilder.get(fieldName, entity);
                    }
                    break;
                }
            }
        }
        return pathBuilder;
    }

    @Override
    public Expression<?> visitEnum(EdmEnumType edmEnumType, List<String> list) throws ExpressionVisitException, ODataApplicationException {
        return null;
    }

    public Class<?> getoDataEntityClass() {
        return oDataEntityClass;
    }

    public void setoDataEntityClass(Class<?> oDataEntityClass) {
        this.oDataEntityClass = oDataEntityClass;
    }
}
