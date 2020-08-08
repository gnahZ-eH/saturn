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

package com.github.saturn.odata.metadata;

import com.github.saturn.odata.exceptions.SaturnODataException;
import com.github.saturn.odata.utils.ClassUtils;
import com.github.saturn.odata.utils.ODataUtils;
import com.github.saturn.odata.annotations.*;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.server.api.ODataApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SaturnEdmProvider extends CsdlAbstractEdmProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SaturnEdmProvider.class);

    private SaturnEdmContext context = new SaturnEdmContext();

    public SaturnEdmProvider initialize() throws ODataApplicationException {
        context.initialize();
        return this;
    }

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
        Class<?> clazz = context.getEntityTypes().get(entityTypeName.getName());
        if (clazz == null) return null;

        ODataEntityType oDataEntityType = clazz.getAnnotation(ODataEntityType.class);
        List<Field> fields = ClassUtils.getFields(clazz);
        List<CsdlProperty> csdlProperties = ODataUtils.getCsdlProperties(fields, context);
        List<CsdlNavigationProperty> csdlNavigationProperties = ODataUtils.getCsdlNavigationProperties(fields, context);
        List<CsdlPropertyRef> csdlPropertyRefs = Arrays.stream(oDataEntityType.keys()).map(key -> new CsdlPropertyRef().setName(key)).collect(Collectors.toList());

        for (CsdlPropertyRef csdlPropertyRef : csdlPropertyRefs) {
            String csdlPropertyRefName = csdlPropertyRef.getName();
            boolean valid = csdlProperties.stream().anyMatch(c -> c.getName().equals(csdlPropertyRefName));
            if (!valid) {
                throw new SaturnODataException(String.format("The key %s does not exist in entity %s", csdlPropertyRefName, entityTypeName));
            }
        }

        return new CsdlEntityType()
                .setName(oDataEntityType.name())
                .setProperties(csdlProperties)
                .setNavigationProperties(csdlNavigationProperties)
                .setKey(csdlPropertyRefs);
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
        Class<?> clazz = context.getEntitySets().get(entitySetName);
        if (clazz == null) return null;

        ODataEntityType oDataEntityType = clazz.getAnnotation(ODataEntityType.class);
        List<Field> fields = ClassUtils.getFields(clazz);
        List<CsdlNavigationPropertyBinding> csdlNavigationPropertyBindings = ODataUtils.getCsdlNavigationPropertyBindings(fields);

        return new CsdlEntitySet()
                .setName(entitySetName)
                .setType(ODataUtils.generateFQN(oDataEntityType.namespace(), oDataEntityType.name()))
                .setNavigationPropertyBindings(csdlNavigationPropertyBindings);
    }

    @Override
    public CsdlComplexType getComplexType(FullQualifiedName complexTypeName) throws ODataException {
        Class<?> clazz = context.getComplexTypes().get(complexTypeName.getName());
        if (clazz == null) return null;

        ODataComplexType oDataComplexType = clazz.getAnnotation(ODataComplexType.class);
        List<Field> fields = ClassUtils.getFields(clazz);
        List<CsdlProperty> csdlProperties = ODataUtils.getCsdlProperties(fields, context);
        List<CsdlNavigationProperty> csdlNavigationProperties = ODataUtils.getCsdlNavigationProperties(fields, context);

        return new CsdlComplexType()
                .setName(oDataComplexType.name())
                .setProperties(csdlProperties)
                .setNavigationProperties(csdlNavigationProperties)
                .setOpenType(oDataComplexType.openType());
    }

    @Override
    public CsdlActionImport getActionImport(FullQualifiedName entityContainer, String actionImportName) throws ODataException {
        Class<?> clazz = context.getActionImports().get(actionImportName);
        if (clazz == null) return null;
        ODataActionImport oDataActionImport = clazz.getAnnotation(ODataActionImport.class);

        return new CsdlActionImport()
                .setName(actionImportName)
                .setEntitySet(oDataActionImport.entitySet())
                .setAction(ODataUtils.generateFQN(oDataActionImport.namespace(), oDataActionImport.name()));
    }

    @Override
    public CsdlFunctionImport getFunctionImport(FullQualifiedName entityContainer, String functionImportName) throws ODataException {
        Class<?> clazz = context.getFunctionImports().get(functionImportName);
        if (clazz == null) return null;
        ODataFunctionImport oDataFunctionImport = clazz.getAnnotation(ODataFunctionImport.class);

        CsdlFunctionImport csdlFunctionImport = new CsdlFunctionImport()
                .setName(functionImportName)
                .setFunction(ODataUtils.generateFQN(oDataFunctionImport.namespace(), oDataFunctionImport.name()));

        if (!oDataFunctionImport.entitySet().trim().isEmpty()) {
            csdlFunctionImport.setEntitySet(oDataFunctionImport.entitySet());
        }

        return csdlFunctionImport;
    }
}
