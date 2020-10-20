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

package com.github.saturn.odata.processors;

import com.github.saturn.odata.annotations.ODataFunction;
import com.github.saturn.odata.exceptions.SaturnODataException;
import com.github.saturn.odata.interfaces.CustomOperation;
import com.github.saturn.odata.interfaces.EntityOperation;
import com.github.saturn.odata.uri.QueryOptions;
import com.github.saturn.odata.utils.ExceptionUtils;

import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.UriParameter;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

public class BaseTypeProcessor extends SaturnProcessor {

    protected Map<String, EntityOperation> entityOperationMap;
    protected Map<String, CustomOperation<?>> functionMap;

    protected UriResource getResourceFromUriInfo(UriInfo uriInfo) {
        return uriInfo.getUriResourceParts().get(uriInfo.getUriResourceParts().size() - 1);
    }

    protected void generateOperationMap(Map<String, EntityOperation> entityOperationMap,
                                            Map<String, CustomOperation<?>> functionMap,
                                            ApplicationContext applicationContext) {
        applicationContext
                .getBeansOfType(EntityOperation.class)
                .forEach((key, entityOperation) -> entityOperationMap.put(entityOperation.forEntity(), entityOperation));

        applicationContext
                .getBeansOfType(CustomOperation.class)
                .forEach((key, customOperation) -> {
                    ODataFunction oDataFunction = ((CustomOperation<?>) customOperation).getClass().getAnnotation(ODataFunction.class);
                    if (oDataFunction != null) {
                        String operationName = oDataFunction.name().isEmpty() ? ((CustomOperation<?>) customOperation).getClass().getSimpleName() : oDataFunction.name();
                        functionMap.put(operationName, customOperation);
                    }
                });
    }

    protected Object readByEntityOperation(UriResourceNavigation uriResourceNavigation, EdmEntitySet edmEntitySet, Object superObject, SelectOption selectOption, ExpandOption expandOption) throws SaturnODataException {

        // can also use EntitySet
        // todo need to test here
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        EntityOperation entityOperation = entityOperationMap.get(edmEntityType.getName());

        ExceptionUtils.assertNotNull(entityOperation, EntityOperation.class.getSimpleName(), edmEntityType.getName());
        List<UriParameter> parameters = uriResourceNavigation.getKeyPredicates();
        Map<String, UriParameter> parameterMap = parameters.stream().collect(Collectors.toMap(UriParameter::getName, p -> p));
        QueryOptions queryOptions = new QueryOptions(expandOption, null, selectOption, null);

        return entityOperation.retrieveByKey(parameterMap, queryOptions, superObject);
    }

    protected Object readByEntityOperation(UriResourceEntitySet uriResourceEntitySet, SelectOption selectOption, ExpandOption expandOption) throws SaturnODataException {

        // can also use EntitySet
        // todo need to test here
        EdmEntityType edmEntityType = uriResourceEntitySet.getEntityType();
        EntityOperation entityOperation = entityOperationMap.get(edmEntityType.getName());

        ExceptionUtils.assertNotNull(entityOperation, EntityOperation.class.getSimpleName(), edmEntityType.getName());
        List<UriParameter> parameters = uriResourceEntitySet.getKeyPredicates();
        Map<String, UriParameter> parameterMap = parameters.stream().collect(Collectors.toMap(UriParameter::getName, p -> p));
        QueryOptions queryOptions = new QueryOptions(expandOption, null, selectOption, null);

        return entityOperation.retrieveByKey(parameterMap, queryOptions, null);
    }

    protected Object readByEntityOperation(UriResourceEntitySet uriResourceEntitySet) throws SaturnODataException {
        return readByEntityOperation(uriResourceEntitySet, null, null);
    }
}
