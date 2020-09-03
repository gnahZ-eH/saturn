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

import com.github.saturn.odata.annotations.*;
import com.github.saturn.odata.utils.ClassUtils;

import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SaturnEdmContext {

    private static final Logger LOG = LoggerFactory.getLogger(SaturnEdmContext.class);

    private String NAME_SPACE = null;
    private String DEFAULT_EDM_PKG = null;
    private String CONTAINER_NAME = null;
    private String SERVICE_ROOT = null;

    private Map<String, Class<?>> entitySets      = new HashMap<>();
    private Map<String, Class<?>> enums           = new HashMap<>();
    private Map<String, Class<?>> actions         = new HashMap<>();
    private Map<String, Class<?>> actionImports   = new HashMap<>();
    private Map<String, Class<?>> functions       = new HashMap<>();
    private Map<String, Class<?>> functionImports = new HashMap<>();
    private Map<String, Class<?>> complexTypes    = new HashMap<>();
    private Map<String, Class<?>> entityTypes     = new HashMap<>();

    public SaturnEdmContext initialize() throws ODataApplicationException {
        ClassPathScanningCandidateComponentProvider provider = ClassUtils.createComponentScanner(Arrays.asList(
                ODataAction.class,
                ODataActionImport.class,
                ODataComplexType.class,
                ODataEntitySet.class,
                ODataEntityType.class,
                ODataEnumType.class,
                ODataFunction.class,
                ODataFunctionImport.class));

        Set<BeanDefinition> beanDefinitions = provider.findCandidateComponents(DEFAULT_EDM_PKG);

        for (BeanDefinition beanDefinition : beanDefinitions) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(beanDefinition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new ODataApplicationException(e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
            }
            ODataAction         action         = clazz.getAnnotation(ODataAction.class);
            ODataActionImport   actionImport   = clazz.getAnnotation(ODataActionImport.class);
            ODataComplexType    complexType    = clazz.getAnnotation(ODataComplexType.class);
            ODataEntitySet      entitySet      = clazz.getAnnotation(ODataEntitySet.class);
            ODataEntityType     entityType     = clazz.getAnnotation(ODataEntityType.class);
            ODataEnumType       enumType       = clazz.getAnnotation(ODataEnumType.class);
            ODataFunction       function       = clazz.getAnnotation(ODataFunction.class);
            ODataFunctionImport functionImport = clazz.getAnnotation(ODataFunctionImport.class);

            if (action != null) {
                String name = action.name().isEmpty() ? clazz.getSimpleName() : action.name();
                actions.put(name, clazz);
                LOG.debug("Action {} is loaded...", name);
            }

            if (actionImport != null) {
                String name = actionImport.name().isEmpty() ? clazz.getSimpleName() : actionImport.name();
                actionImports.put(name, clazz);
                LOG.debug("Action {} is loaded...", name);
            }

            if (complexType != null) {
                String name = complexType.name().isEmpty() ? clazz.getSimpleName() : complexType.name();
                complexTypes.put(name, clazz);
                LOG.debug("ComplexType {} is loaded...", name);
            }

            if (entitySet != null) {
                String name = entitySet.name().isEmpty() ? clazz.getSimpleName() : entitySet.name();
                entitySets.put(name, clazz);
                LOG.debug("EntitySet {} is loaded...", name);
            }

            if (entityType != null) {
                String name = entityType.name().isEmpty() ? clazz.getSimpleName() : entityType.name();
                entityTypes.put(name, clazz);
                LOG.debug("EntityType {} is loaded...", name);
            }

            if (enumType != null) {
                String name = enumType.name().isEmpty() ? clazz.getSimpleName() : enumType.name();
                enums.put(name, clazz);
                LOG.debug("EnumType {} is loaded...", name);
            }

            if (function != null) {
                String name = function.name().isEmpty() ? clazz.getSimpleName() : function.name();
                functions.put(name, clazz);
                LOG.debug("Function {} is loaded...", name);
            }

            if (functionImport != null) {
                String name = functionImport.name().isEmpty() ? clazz.getSimpleName() : functionImport.name();
                functionImports.put(name, clazz);
                LOG.debug("FunctionImport {} is loaded...", name);
            }
        }
        return this;
    }

    public String getNameSpace() {
        return NAME_SPACE;
    }

    public void setNameSpace(String NAME_SPACE) {
        this.NAME_SPACE = NAME_SPACE;
    }

    public String getDefaultEdmPkg() {
        return DEFAULT_EDM_PKG;
    }

    public void setDefaultEdmPkg(String DEFAULT_EDM_PKG) {
        this.DEFAULT_EDM_PKG = DEFAULT_EDM_PKG;
    }

    public String getContainerName() {
        return CONTAINER_NAME;
    }

    public void setContainerName(String CONTAINER_NAME) {
        this.CONTAINER_NAME = CONTAINER_NAME;
    }

    public String getServiceRoot() {
        return SERVICE_ROOT;
    }

    public void setServiceRoot(String SERVICE_ROOT) {
        this.SERVICE_ROOT = SERVICE_ROOT;
    }

    public Map<String, Class<?>> getEntitySets() {
        return entitySets;
    }

    public Map<String, Class<?>> getEnums() {
        return enums;
    }

    public Map<String, Class<?>> getActions() {
        return actions;
    }

    public Map<String, Class<?>> getActionImports() {
        return actionImports;
    }

    public Map<String, Class<?>> getFunctions() {
        return functions;
    }

    public Map<String, Class<?>> getFunctionImports() {
        return functionImports;
    }

    public Map<String, Class<?>> getComplexTypes() {
        return complexTypes;
    }

    public Map<String, Class<?>> getEntityTypes() {
        return entityTypes;
    }
}
