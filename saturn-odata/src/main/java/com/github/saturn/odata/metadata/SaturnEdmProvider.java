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
import com.github.saturn.odata.annotations.ODataEntityType;
import com.github.saturn.odata.annotations.ODataFunction;
import com.github.saturn.odata.annotations.ODataEnumType;
import com.github.saturn.odata.annotations.ODataComplexType;
import com.github.saturn.odata.annotations.ODataAction;
import com.github.saturn.odata.annotations.ODataActionImport;
import com.github.saturn.odata.annotations.ODataFunctionImport;

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
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumMember;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlStructuralType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.server.api.ODataApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Comparator;
import java.util.Collections;
import java.util.stream.Collectors;

public class SaturnEdmProvider extends CsdlAbstractEdmProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SaturnEdmProvider.class);

    private SaturnEdmContext context = new SaturnEdmContext();

    public SaturnEdmProvider initialize() throws ODataApplicationException {
        context.initialize();
        return this;
    }

    @Override
    public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
        Class<?> clazz = context.getEntityTypes().get(entityTypeName.getName());
        if (clazz == null) {
            return null;
        }

        ODataEntityType oDataEntityType = clazz.getAnnotation(ODataEntityType.class);
        List<Field> fields = ClassUtils.getFields(clazz);
        List<CsdlProperty> csdlProperties = ODataUtils.getCsdlProperties(fields, context.getNameSpace());
        List<CsdlNavigationProperty> csdlNavigationProperties = ODataUtils.getCsdlNavigationProperties(fields, context.getNameSpace());
        List<CsdlPropertyRef> csdlPropertyRefs = Arrays.stream(oDataEntityType.keys()).map(key -> new CsdlPropertyRef().setName(key)).collect(Collectors.toList());

        for (CsdlPropertyRef csdlPropertyRef : csdlPropertyRefs) {
            String csdlPropertyRefName = csdlPropertyRef.getName();
            boolean valid = csdlProperties.stream().anyMatch(c -> c.getName().equals(csdlPropertyRefName));
            if (!valid) {
                throw new SaturnODataException("The key %s does not exist in entity %s", csdlPropertyRefName, entityTypeName);
            }
        }

        return new CsdlEntityType()
                .setName(oDataEntityType.name())
                .setProperties(csdlProperties)
                .setNavigationProperties(csdlNavigationProperties)
                .setKey(csdlPropertyRefs);
    }

    @Override
    public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName) throws ODataException {
        Class<?> clazz = context.getEntitySets().get(entitySetName);
        if (clazz == null) {
            return null;
        }

        ODataEntityType oDataEntityType = clazz.getAnnotation(ODataEntityType.class);
        List<Field> fields = ClassUtils.getFields(clazz);
        List<CsdlNavigationPropertyBinding> csdlNavigationPropertyBindings = ODataUtils.getCsdlNavigationPropertyBindings(fields);

        return new CsdlEntitySet()
                .setName(entitySetName)
                .setType(ODataUtils.generateFQN(oDataEntityType.namespace(), oDataEntityType.name()))
                .setNavigationPropertyBindings(csdlNavigationPropertyBindings);
    }

    @Override
    public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
        Class<?> clazz = context.getComplexTypes().get(complexTypeName.getName());
        if (clazz == null) {
            return null;
        }

        ODataComplexType oDataComplexType = clazz.getAnnotation(ODataComplexType.class);
        List<Field> fields = ClassUtils.getFields(clazz);
        List<CsdlProperty> csdlProperties = ODataUtils.getCsdlProperties(fields, context.getNameSpace());
        List<CsdlNavigationProperty> csdlNavigationProperties = ODataUtils.getCsdlNavigationProperties(fields, context.getNameSpace());

        return new CsdlComplexType()
                .setName(oDataComplexType.name())
                .setProperties(csdlProperties)
                .setNavigationProperties(csdlNavigationProperties)
                .setOpenType(oDataComplexType.openType());
    }

    @Override
    public CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName) throws ODataException {
        Class<?> clazz = context.getActionImports().get(actionImportName);
        if (clazz == null) {
            return null;
        }
        ODataActionImport oDataActionImport = clazz.getAnnotation(ODataActionImport.class);

        return new CsdlActionImport()
                .setName(actionImportName)
                .setEntitySet(oDataActionImport.entitySet())
                .setAction(ODataUtils.generateFQN(oDataActionImport.namespace(), oDataActionImport.name()));
    }

    @Override
    public CsdlFunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String functionImportName) throws ODataException {
        Class<?> clazz = context.getFunctionImports().get(functionImportName);
        if (clazz == null) {
            return null;
        }
        ODataFunctionImport oDataFunctionImport = clazz.getAnnotation(ODataFunctionImport.class);

        CsdlFunctionImport csdlFunctionImport = new CsdlFunctionImport()
                .setName(functionImportName)
                .setFunction(ODataUtils.generateFQN(oDataFunctionImport.namespace(), oDataFunctionImport.name()));

        if (!oDataFunctionImport.entitySet().trim().isEmpty()) {
            csdlFunctionImport.setEntitySet(oDataFunctionImport.entitySet());
        }

        return csdlFunctionImport;
    }

    @Override
    public CsdlEnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
        Class<?> clazz = context.getEnums().get(enumTypeName.getName());
        if (clazz == null) {
            return null;
        }

        if (clazz.isEnum()) {
            ODataEnumType oDataEnumType = clazz.getAnnotation(ODataEnumType.class);
            Object[] enumConstants = clazz.getEnumConstants();
            CsdlEnumType csdlEnumType = new CsdlEnumType()
                    .setName(enumTypeName.getName())
                    .setUnderlyingType(oDataEnumType.underlyingType().getType());

            for (Object object : enumConstants) {
                Enum<?> constant = (Enum<?>) object;

                try {
                    String ordinalVal = String.valueOf(constant.ordinal());
                    CsdlEnumMember csdlEnumMember = new CsdlEnumMember()
                            .setName(object.toString())
                            .setValue(ordinalVal);
                    csdlEnumType.getMembers().add(csdlEnumMember);
                } catch (IllegalArgumentException | SecurityException e) {
                    throw new SaturnODataException(e);
                }
            }
            return csdlEnumType;
        }
        throw new SaturnODataException("%s is not an enum type", enumTypeName.getName());
    }

    @Override
    public List<CsdlFunction> getFunctions(final FullQualifiedName functionName) throws ODataException {
        CsdlFunction csdlFunction = ODataUtils.getFunction(functionName, context);
        return csdlFunction == null ? null : Collections.singletonList(csdlFunction);
    }

    @Override
    public List<CsdlAction> getActions(final FullQualifiedName actionName) throws ODataException {
        CsdlAction csdlAction = ODataUtils.getAction(actionName, context);
        return csdlAction == null ? null : Collections.singletonList(csdlAction);
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {

        CsdlSchema csdlSchema = new CsdlSchema();
        csdlSchema.setNamespace(context.getNameSpace());

        List<CsdlEntityType>  csdlEntityTypeList  = new ArrayList<>();
        List<CsdlEnumType>    csdlEnumTypeList    = new ArrayList<>();
        List<CsdlAction>      csdlActionList      = new ArrayList<>();
        List<CsdlFunction>    csdlFunctionList    = new ArrayList<>();
        List<CsdlComplexType> csdlComplexTypeList = new ArrayList<>();

        for (Map.Entry<String, Class<?>> entry : context.getEntityTypes().entrySet()) {
            ODataEntityType oDataEntityType = entry.getValue().getAnnotation(ODataEntityType.class);
            String namespace = oDataEntityType.namespace().isEmpty() ? context.getNameSpace() : oDataEntityType.namespace();
            String name = oDataEntityType.name().isEmpty() ? entry.getValue().getSimpleName() : oDataEntityType.name();
            csdlEntityTypeList.add(getEntityType(ODataUtils.generateFQN(namespace, name)));
        }

        for (Map.Entry<String, Class<?>> entry : context.getEnums().entrySet()) {
            ODataEnumType oDataEnumType = entry.getValue().getAnnotation(ODataEnumType.class);
            String namespace = oDataEnumType.namespace().isEmpty() ? context.getNameSpace() : oDataEnumType.namespace();
            String name = oDataEnumType.name().isEmpty() ? entry.getValue().getSimpleName() : oDataEnumType.name();
            csdlEnumTypeList.add(getEnumType(ODataUtils.generateFQN(namespace, name)));
        }

        for (Map.Entry<String, Class<?>> entry : context.getActions().entrySet()) {
            ODataAction oDataAction = entry.getValue().getAnnotation(ODataAction.class);
            String namespace = oDataAction.namespace().isEmpty() ? context.getNameSpace() : oDataAction.namespace();
            String name = oDataAction.name().isEmpty() ? entry.getValue().getSimpleName() : oDataAction.name();
            csdlActionList.add(ODataUtils.getAction(namespace, name, context));
        }

        for (Map.Entry<String, Class<?>> entry : context.getFunctions().entrySet()) {
            ODataFunction oDataFunction = entry.getValue().getAnnotation(ODataFunction.class);
            String namespace = oDataFunction.namespace().isEmpty() ? context.getNameSpace() : oDataFunction.namespace();
            String name = oDataFunction.name().isEmpty() ? entry.getValue().getSimpleName() : oDataFunction.name();
            csdlFunctionList.add(ODataUtils.getFunction(namespace, name, context));
        }

        for (Map.Entry<String, Class<?>> entry : context.getComplexTypes().entrySet()) {
            ODataComplexType oDataComplexType = entry.getValue().getAnnotation(ODataComplexType.class);
            String namespace = oDataComplexType.namespace().isEmpty() ? context.getNameSpace() : oDataComplexType.namespace();
            String name = oDataComplexType.name().isEmpty() ? entry.getValue().getSimpleName() : oDataComplexType.name();
            csdlComplexTypeList.add(getComplexType(ODataUtils.generateFQN(namespace, name)));
        }

        csdlSchema.setEntityTypes(csdlEntityTypeList.stream().sorted(Comparator.comparing(CsdlStructuralType::getName)).collect(Collectors.toList()));
        csdlSchema.setEnumTypes(csdlEnumTypeList);
        csdlSchema.setActions(csdlActionList);
        csdlSchema.setFunctions(csdlFunctionList);
        csdlSchema.setComplexTypes(csdlComplexTypeList);
        csdlSchema.setEntityContainer(getEntityContainer());

        return Collections.singletonList(csdlSchema);
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {

        CsdlEntityContainer csdlEntityContainer = new CsdlEntityContainer();
        csdlEntityContainer.setName(context.getContainerName());
        FullQualifiedName container = ODataUtils.generateFQN(context.getNameSpace(), context.getContainerName());

        List<CsdlEntitySet>      csdlEntitySetList      = new ArrayList<>();
        List<CsdlActionImport>   csdlActionImportList   = new ArrayList<>();
        List<CsdlFunctionImport> csdlFunctionImportList = new ArrayList<>();

        for (Map.Entry<String, Class<?>> entry : context.getEntitySets().entrySet()) {
            csdlEntitySetList.add(getEntitySet(container, entry.getKey()));
        }

        for (Map.Entry<String, Class<?>> entry : context.getActionImports().entrySet()) {
            csdlActionImportList.add(getActionImport(container, entry.getKey()));
        }

        for (Map.Entry<String, Class<?>> entry : context.getFunctionImports().entrySet()) {
            csdlFunctionImportList.add(getFunctionImport(container, entry.getKey()));
        }

        csdlEntityContainer.setEntitySets(csdlEntitySetList);
        csdlEntityContainer.setActionImports(csdlActionImportList);
        csdlEntityContainer.setFunctionImports(csdlFunctionImportList);

        return csdlEntityContainer;
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName) throws ODataException {
        FullQualifiedName container = ODataUtils.generateFQN(context.getNameSpace(), context.getContainerName());
        if (ODataUtils.isNull(entityContainerName) || entityContainerName.equals(container)) {
            return new CsdlEntityContainerInfo().setContainerName(container);
        }
        return null;
    }
}
