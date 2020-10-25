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
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

public class QueryExpressionVisitor extends AbstractExpressionVisitor {

    private static final Logger LOG = LoggerFactory.getLogger(QueryExpressionVisitor.class);

    // JPA Entity and OData Edm are combined
    private Class<?> entityClass;

    public QueryExpressionVisitor(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Expression<?> visitMember(Member member) throws ExpressionVisitException, ODataApplicationException {

        UriInfoResource uriInfoResource = member.getResourcePath();
        ODataEntityType oDataEntityType = entityClass.getAnnotation(ODataEntityType.class);
        PathBuilder<?> pathBuilder = new PathBuilder<>(oDataEntityType.jpaEntity(), oDataEntityType.jpaVariable());

        for (UriResource uriResource : uriInfoResource.getUriResourceParts()) {

            switch (uriResource.getKind()) {
                case primitiveProperty:
                    UriResourcePrimitiveProperty primitiveProperty = (UriResourcePrimitiveProperty) uriResource;
                    String propertyName = primitiveProperty.getProperty().getName();
                    Field field = ClassUtils.getFieldFromEdmClass(entityClass, propertyName);
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
                            entityClass = field.getType();
                        }
                    } catch (SaturnODataException e) {
                        // just for assert part
                        LOG.error(e.getMessage());
                    }
                    return getJPAEntityPath(oDataEntityType.jpaEntity(), pathBuilder, jpaEntityFieldNameWithPath);
                // todo
                default:
                    break;
            }
        }
        return null;
    }

    private Expression<?> getJPAEntityPath(Class<?> jpaEntity, Path<?> pathBuilder, String jpaEntityFieldNameWithPath) {
        return null;
    }

    @Override
    public Expression<?> visitEnum(EdmEnumType edmEnumType, List<String> list) throws ExpressionVisitException, ODataApplicationException {
        return null;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }
}
