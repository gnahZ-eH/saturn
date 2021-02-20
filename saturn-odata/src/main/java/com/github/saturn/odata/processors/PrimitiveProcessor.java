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

import com.github.saturn.odata.exceptions.SaturnODataException;
import com.github.saturn.odata.metadata.SaturnEdmContext;
import com.github.saturn.odata.utils.ExceptionUtils;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.PrimitiveCollectionProcessor;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class PrimitiveProcessor extends BaseTypeProcessor implements org.apache.olingo.server.api.processor.PrimitiveProcessor, PrimitiveCollectionProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PrimitiveProcessor.class);

    public PrimitiveProcessor initialize(SaturnEdmContext saturnEdmContext, ApplicationContext applicationContext) {
        super.initialize(saturnEdmContext);
        super.generateOperationMap(applicationContext);
        return this;
    }

    @Override
    public void readPrimitive(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws ODataApplicationException, ODataLibraryException {
        UriResource resource = getResourceFromUriInfo(uriInfo);

        if (resource instanceof UriResourceEntitySet) {
            try {
                readPrimitive(oDataResponse, uriInfo, contentType);
            } catch (SaturnODataException e) {
                LOG.error(e.getMessage());
            }
        } else {
            throw new ODataApplicationException("Haven't been implemented yet.", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
        }
    }

    private void readPrimitive(ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws SaturnODataException, SerializerException, ODataApplicationException {
        UriResource resource = getResourceFromUriInfo(uriInfo);
        UriResourceProperty uriResourceProperty = (UriResourceProperty) resource;
        EdmProperty edmProperty = uriResourceProperty.getProperty();
        EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) edmProperty.getType();
        String propertyName = edmProperty.getName();

        // pre-last segment should be an entity type
        UriResource preResource = uriInfo.getUriResourceParts().get(uriInfo.getUriResourceParts().size() - 2);
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) preResource;
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

        //--------------------------------- Read Entity ------------------------------------------------------
        Entity entity;
        Object object = super.readByEntityOperation(uriResourceEntitySet);
        ExceptionUtils.assertNotNull(object, uriResourceEntitySet.getEntityType().getClass().getSimpleName());

        //--------------------------------- trans to entity --------------------------------------------------
        try {
            entity = fromObject2Entity(object);
        } catch (IllegalAccessException e) {
            throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        //--------------------------------- get the property data from the entity ----------------------------
        Property property = entity.getProperty(propertyName);
        ExceptionUtils.assertNotNull(property, Property.class.getSimpleName(), propertyName);

        //--------------------------------- generate response ------------------------------------------------
        Object value = property.getValue();

        if (value == null) {
            oDataResponse.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
        } else {
            ODataSerializer oDataSerializer = odata.createSerializer(contentType);
            ContextURL contextURL;

            try {
                contextURL = ContextURL
                        .with()
                        .entitySet(edmEntitySet)
                        .navOrPropertyPath(propertyName)
                        .serviceRoot(new URI(saturnEdmContext.getServiceRoot()))
                        .build();
            } catch (URISyntaxException e) {
                throw new SaturnODataException(HttpStatusCode.BAD_REQUEST, e.getMessage());
            }

            PrimitiveSerializerOptions primitiveSerializerOptions = PrimitiveSerializerOptions
                    .with()
                    .contextURL(contextURL)
                    .build();

            try {
                SerializerResult serializerResult;
                if (property.isCollection()) {
                    serializerResult = oDataSerializer.primitiveCollection(serviceMetadata, edmPrimitiveType, property, primitiveSerializerOptions);
                } else {
                    serializerResult = oDataSerializer.primitive(serviceMetadata, edmPrimitiveType, property, primitiveSerializerOptions);
                }

                InputStream serializedContent = serializerResult.getContent();

                oDataResponse.setContent(serializedContent);
                oDataResponse.setStatusCode(HttpStatusCode.OK.getStatusCode());
                oDataResponse.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());

            } catch (SerializerException e) {
                LOG.error(e.getMessage(), e);
                throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    @Override
    public void updatePrimitive(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType, ContentType contentType1) throws ODataApplicationException, ODataLibraryException {

    }

    @Override
    public void deletePrimitive(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo) throws ODataApplicationException, ODataLibraryException {

    }

    @Override
    public void readPrimitiveCollection(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws ODataApplicationException, ODataLibraryException {

    }

    @Override
    public void updatePrimitiveCollection(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType, ContentType contentType1) throws ODataApplicationException, ODataLibraryException {

    }

    @Override
    public void deletePrimitiveCollection(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo) throws ODataApplicationException, ODataLibraryException {

    }
}
