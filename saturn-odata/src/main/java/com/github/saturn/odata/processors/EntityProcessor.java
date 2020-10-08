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

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
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
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.springframework.context.ApplicationContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityProcessor extends SaturnProcessor implements org.apache.olingo.server.api.processor.EntityProcessor, EntityCollectionProcessor {

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

//        if (resource instanceof UriResourceEntitySet) {
//
//        }
    }

    // todo need to be tested
    private void readEntity(ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws SaturnODataException {

        List<UriResource> uriResourceParts = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) uriResourceParts.get(0);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        SelectOption selectOption = uriInfo.getSelectOption();
        ExpandOption expandOption = uriInfo.getExpandOption();
        String selects;

        try {
            selects = getOData().createUriHelper().buildContextURLSelectList(edmEntityType, null, selectOption);
        } catch (SerializerException e) {
            throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        Entity entity;
        Object object = readByEntityOperation(uriResourceEntitySet, selectOption, expandOption);
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
                    .serviceRoot(new URI(getSaturnEdmContext().getServiceRoot()))
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
            oDataSerializer = getOData().createSerializer(contentType);
            serializerResult = oDataSerializer.entity(getServiceMetadata(), edmEntityType, entity, serializerOptions);
        } catch (SerializerException e) {
            throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        oDataResponse.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
        oDataResponse.setContent(serializerResult.getContent());
        oDataResponse.setStatusCode(HttpStatusCode.OK.getStatusCode());
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

    }

    private UriResource getResourceFromUriInfo(UriInfo uriInfo) {
        return uriInfo.getUriResourceParts().get(uriInfo.getUriResourceParts().size() - 1);
    }
}
