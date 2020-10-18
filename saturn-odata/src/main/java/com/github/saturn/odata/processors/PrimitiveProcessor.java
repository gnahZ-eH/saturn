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

import com.github.saturn.odata.interfaces.CustomOperation;
import com.github.saturn.odata.interfaces.EntityOperation;
import com.github.saturn.odata.metadata.SaturnEdmContext;
import com.github.saturn.odata.utils.ODataUtils;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.processor.PrimitiveCollectionProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PrimitiveProcessor extends SaturnProcessor implements org.apache.olingo.server.api.processor.PrimitiveProcessor, PrimitiveCollectionProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PrimitiveProcessor.class);
    private Map<String, EntityOperation> entityOperationMap;
    private Map<String, CustomOperation<?>> functionMap;

    public PrimitiveProcessor initialize(SaturnEdmContext saturnEdmContext, ApplicationContext applicationContext) {
        super.initialize(saturnEdmContext);
        ODataUtils.generateOperationMap(entityOperationMap, functionMap, applicationContext);
        return this;
    }

    @Override
    public void readPrimitive(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws ODataApplicationException, ODataLibraryException {
        UriResource resource = getResourceFromUriInfo(uriInfo);

    }

    private void readPrimitive(ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) {
        UriResource resource = getResourceFromUriInfo(uriInfo);
        UriResourceProperty uriResourceProperty = (UriResourceProperty) resource;
        EdmProperty edmProperty = uriResourceProperty.getProperty();
        EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) edmProperty.getType();
        String propertyName = edmProperty.getName();

        // pre-last segment should be an entity type
        UriResource preResource = uriInfo.getUriResourceParts().get(uriInfo.getUriResourceParts().size() - 2);
        EdmEntitySet edmEntitySet = ((UriResourceEntitySet) preResource).getEntitySet();

        //--------------------------------- Read Entity ---------------------------------------
        Entity entity;
        Object object = readEntity(edmEntitySet);
        // todo
    }

    private Object readEntity(EdmEntitySet edmEntitySet) {
        // todo
        return null;
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
