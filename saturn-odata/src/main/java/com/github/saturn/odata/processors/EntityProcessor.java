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

import com.github.saturn.odata.annotations.ODataEntityType;
import com.github.saturn.odata.annotations.ODataFunction;
import com.github.saturn.odata.exceptions.SaturnODataException;
import com.github.saturn.odata.interfaces.CustomOperation;
import com.github.saturn.odata.interfaces.EntityOperation;
import com.github.saturn.odata.metadata.SaturnEdmContext;
import com.github.saturn.odata.uri.QueryOptions;
import com.github.saturn.odata.utils.ExceptionUtils;
import com.github.saturn.odata.utils.StringUtils;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.core.uri.queryoption.TopOptionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityProcessor extends SaturnProcessor implements org.apache.olingo.server.api.processor.EntityProcessor, EntityCollectionProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(EntityProcessor.class);
    private Map<String, EntityOperation> entityOperationMap;
    private Map<String, CustomOperation<?>> functionMap;

    public EntityProcessor initialize(SaturnEdmContext saturnEdmContext, ApplicationContext applicationContext) {

        super.initialize(saturnEdmContext);
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
        return this;
    }

    @Override
    public void readEntity(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws ODataApplicationException, ODataLibraryException {
        UriResource resource = getResourceFromUriInfo(uriInfo);

        if (resource instanceof UriResourceEntitySet) {
            try {
                readEntity(oDataResponse, uriInfo, contentType);
            } catch (SaturnODataException e) {
                LOG.error(e.getMessage());
            }
        } else if (resource instanceof UriResourceNavigation) {
            try {
                readNaviEntity(oDataResponse, uriInfo, contentType);
            } catch (SaturnODataException e) {
                LOG.error(e.getMessage());
            }
        } else {
            throw new ODataApplicationException("Haven't been implemented yet.", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
        }
    }

    // todo need to be tested
    // only one result will be returned
    private void readEntity(ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws SaturnODataException {

        List<UriResource> uriResourceParts = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResourceParts.get(0);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        processReadEntity(
                oDataResponse,
                uriInfo,
                contentType,
                edmEntityType,
                edmEntitySet,
                uriResourceEntitySet,
                null,
                null,
                false);
    }

    // todo need to be tested
    private void readNaviEntity(ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws SaturnODataException {

        List<UriResource> uriResourceParts = uriInfo.getUriResourceParts();

        if (uriResourceParts.size() != 2) {
            throw new SaturnODataException(HttpStatusCode.NOT_IMPLEMENTED, "Haven't been implemented yet.");
        }

        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResourceParts.get(0);
        UriResourceNavigation uriResourceNavigation = (UriResourceNavigation) uriResourceParts.get(1);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        Object superObject = readByEntityOperation(uriResourceEntitySet);
        ExceptionUtils.assertNotNull(superObject, ODataEntityType.class.getSimpleName());

        // navi part
        EdmNavigationProperty edmNavigationProperty = uriResourceNavigation.getProperty();
        EdmEntitySet naviEdmEntitySet = getNavigationEntitySet(edmEntitySet, edmNavigationProperty);

        processReadEntity(
                oDataResponse,
                uriInfo,
                contentType,
                edmEntityType,
                naviEdmEntitySet,
                null,
                uriResourceNavigation,
                superObject,
                true);
    }

    private Object readByEntityOperation(UriResourceNavigation uriResourceNavigation, EdmEntitySet edmEntitySet, Object superObject, SelectOption selectOption, ExpandOption expandOption) throws SaturnODataException {

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

    private Object readByEntityOperation(UriResourceEntitySet uriResourceEntitySet, SelectOption selectOption, ExpandOption expandOption) throws SaturnODataException {

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

    private Object readByEntityOperation(UriResourceEntitySet uriResourceEntitySet) throws SaturnODataException {
        return readByEntityOperation(uriResourceEntitySet, null, null);
    }

    private void processReadEntity(
            ODataResponse oDataResponse,
            UriInfo uriInfo,
            ContentType contentType,
            EdmEntityType edmEntityType,
            EdmEntitySet edmEntitySet,
            UriResourceEntitySet uriResourceEntitySet,
            UriResourceNavigation uriResourceNavigation,
            Object superObject,
            boolean isNavi) throws SaturnODataException {

        SelectOption selectOption = uriInfo.getSelectOption();
        ExpandOption expandOption = uriInfo.getExpandOption();
        String selects;

        try {
            selects = odata.createUriHelper().buildContextURLSelectList(edmEntityType, null, selectOption);
        } catch (SerializerException e) {
            throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        Entity entity;
        Object object;

        if (isNavi) {
            object = readByEntityOperation(uriResourceNavigation, edmEntitySet, superObject, selectOption, expandOption);
        } else {
            object = readByEntityOperation(uriResourceEntitySet, selectOption, expandOption);
        }

        ExceptionUtils.assertNotNull(object, ODataEntityType.class.getSimpleName());

        try {
            entity = fromObject2Entity(object, expandOption);
        } catch (IllegalAccessException e) {
            throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        ContextURL contextURL;

        try {
            contextURL = ContextURL
                    .with()
                    .serviceRoot(new URI(saturnEdmContext.getServiceRoot()))
                    .entitySet(edmEntitySet)
                    .selectList(selects)
                    .suffix(ContextURL.Suffix.ENTITY)
                    .build();
        } catch (URISyntaxException e) {
            throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        EntitySerializerOptions serializerOptions = EntitySerializerOptions
                .with()
                .contextURL(contextURL)
                .select(selectOption)
                .expand(expandOption)
                .build();

        ODataSerializer oDataSerializer;
        SerializerResult serializerResult;

        try {
            oDataSerializer = odata.createSerializer(contentType);
            serializerResult = oDataSerializer.entity(getServiceMetadata(), edmEntityType, entity, serializerOptions);
        } catch (SerializerException e) {
            throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        oDataResponse.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
        oDataResponse.setContent(serializerResult.getContent());
        oDataResponse.setStatusCode(HttpStatusCode.OK.getStatusCode());
    }

    @Override
    public void createEntity(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType, ContentType contentType1) throws ODataApplicationException, ODataLibraryException {

    }

    @Override
    public void updateEntity(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType, ContentType contentType1) throws ODataApplicationException, ODataLibraryException {

    }

    @Override
    public void deleteEntity(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo) throws ODataApplicationException, ODataLibraryException {

    }

    @Override
    public void readEntityCollection(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws ODataApplicationException, ODataLibraryException {
        UriResource resource = getResourceFromUriInfo(uriInfo);

        if (resource instanceof UriResourceEntitySet) {
            try {
                readEntities(oDataRequest, oDataResponse, uriInfo, contentType);
            } catch (SaturnODataException e) {
                LOG.error(e.getMessage());
            }
        } else {
            // todo
            throw new ODataApplicationException("Haven't been implemented yet.", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
        }
    }

    private void readEntities(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws SaturnODataException, SerializerException {

        List<UriResource> uriResourceParts = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResourceParts.get(0);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        SelectOption  selectOption  = uriInfo.getSelectOption();
        ExpandOption  expandOption  = uriInfo.getExpandOption();
        FilterOption  filterOption  = uriInfo.getFilterOption();
        SkipOption    skipOption    = uriInfo.getSkipOption();
        OrderByOption orderByOption = uriInfo.getOrderByOption();
        CountOption   countOption   = uriInfo.getCountOption();
        TopOption     topOption     = uriInfo.getTopOption();

        Integer topMax = saturnEdmContext.getTopMaxValue();
        boolean count = countOption != null && countOption.getValue();

        if (topOption == null && topMax != null) {
            topOption = new TopOptionImpl().setValue(topMax);
        } else if (topOption != null && topMax != null) {
            topOption = ((TopOptionImpl) topOption).setValue(Math.min(topOption.getValue(), topMax));
        }

        String selects;

        try {
            selects = odata.createUriHelper().buildContextURLSelectList(edmEntityType, null, selectOption);
        } catch (SerializerException e) {
            throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        EntityOperation entityOperation = entityOperationMap.get(edmEntityType.getName());
        ExceptionUtils.assertNotNull(entityOperation, EntityOperation.class.getSimpleName(), edmEntityType.getName());

        EntityCollection entityCollection = new EntityCollection();
        List<Entity> resultEntities = entityCollection.getEntities();

        Map<String, String> queryParams = new HashMap<>();
        String requestPath = oDataRequest.getRawBaseUri() + oDataRequest.getRawODataPath();

        if (oDataRequest.getRawQueryPath() != null) {
            Stream.of(oDataRequest.getRawQueryPath().split(StringUtils.AND)).forEach(param -> {
                String[] kv = param.split(StringUtils.EQ);
                String key = kv[0];
                String value = kv[1];
                queryParams.put(key, value);
            });
        }

        if (!queryParams.containsKey(StringUtils.COUNT) && !queryParams.containsKey(StringUtils.COUNT_URL)) {
            queryParams.put(StringUtils.COUNT, StringUtils.TRUE);
        }

        queryParams.remove(StringUtils.SKIP);
        queryParams.remove(StringUtils.SKIP_URL);

        //--------------------------------- Build query option and do query ---------------------------------------
        QueryOptions queryOptions = new QueryOptions(expandOption, filterOption, null, orderByOption);
        queryOptions.setDefaultSkip(saturnEdmContext.isDefaultSkip());
        queryOptions.setDefaultTop(saturnEdmContext.isDefaultTop());

        if (skipOption != null) {
            queryOptions.setSkip(skipOption.getValue());
        }

        if (topOption != null) {
            queryOptions.setTop(topOption.getValue());
        }

        List<?> objects = entityOperation.retrieveAll(queryOptions, null);

        if (count) {
            entityCollection.setCount(objects.size());
        }

        //--------------------------------- Set skip option ---------------------------------------
        if (skipOption != null) {
            if (saturnEdmContext.isDefaultSkip()) {
                objects = objects.subList(skipOption.getValue(), objects.size() - 1);
            }
            queryParams.put(StringUtils.SKIP, String.valueOf(skipOption.getValue() + (topMax != null ? topMax : 0)));
        } else {
            queryParams.put(StringUtils.SKIP, String.valueOf(topMax != null ? topMax : 0));
        }

        //--------------------------------- Generate next link ---------------------------------------
        List<String> queryParamsList = new ArrayList<>();
        queryParams.forEach((key, value) -> queryParamsList.add(key + StringUtils.EQ + value));
        String nextLink = String.join(StringUtils.AND, queryParamsList);

        if (StringUtils.isNotEmpty(nextLink)) {
            nextLink = requestPath + StringUtils.QUESTION_MARK + nextLink;
        }

        //--------------------------------- Set top option ---------------------------------------
        if (topOption != null) {
            if (saturnEdmContext.isDefaultTop()) {
                if (objects.size() <= topOption.getValue()) {
                    nextLink = null;
                }
                objects = objects.subList(0, topOption.getValue());
            } else {
                long allCount = entityOperation.count(queryOptions);
                if (allCount - (skipOption == null ? 0 : skipOption.getValue()) <= topOption.getValue()) {
                    nextLink = null;
                }
            }
        }

        //--------------------------------- trans to entity ---------------------------------------
        for (Object o : objects) {
            try {
                Entity entity = fromObject2Entity(o, expandOption);
                resultEntities.add(entity);
            } catch (SaturnODataException | IllegalAccessException e) {
                throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }

        //--------------------------------------------------------------------------------------
        ODataSerializer oDataSerializer = odata.createSerializer(contentType);
        ContextURL contextURL;

        try {
            if (nextLink != null) {

                    entityCollection.setNext(new URI(nextLink));

            }
            contextURL = ContextURL
                    .with()
                    .entitySet(edmEntitySet)
                    .selectList(selects)
                    .serviceRoot(new URI(saturnEdmContext.getServiceRoot()))
                    .build();
        } catch (URISyntaxException e) {
            throw new SaturnODataException(HttpStatusCode.BAD_REQUEST, e.getMessage());
        }

        EntityCollectionSerializerOptions entityCollectionSerializerOptions = EntityCollectionSerializerOptions
                .with()
                .id(requestPath)
                .contextURL(contextURL)
                .count(countOption)
                .select(selectOption)
                .expand(expandOption)
                .build();

        try {
            SerializerResult serializerResult = oDataSerializer.entityCollection(serviceMetadata, edmEntityType, entityCollection, entityCollectionSerializerOptions);
            InputStream serializedContent = serializerResult.getContent();

            oDataResponse.setContent(serializedContent);
            oDataResponse.setStatusCode(HttpStatusCode.OK.getStatusCode());
            oDataResponse.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());

        } catch (SerializerException e) {
            LOG.error(e.getMessage(), e);
            throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private UriResource getResourceFromUriInfo(UriInfo uriInfo) {
        return uriInfo.getUriResourceParts().get(uriInfo.getUriResourceParts().size() - 1);
    }
}
