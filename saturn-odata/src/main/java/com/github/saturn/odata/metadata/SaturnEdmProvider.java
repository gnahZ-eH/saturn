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
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SaturnEdmProvider extends CsdlAbstractEdmProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SaturnEdmProvider.class);

    private Map<String, Class<?>> entitySets      = new HashMap<>();
    private Map<String, Class<?>> enums           = new HashMap<>();
    private Map<String, Class<?>> actions         = new HashMap<>();
    private Map<String, Class<?>> actionImports   = new HashMap<>();
    private Map<String, Class<?>> functions       = new HashMap<>();
    private Map<String, Class<?>> functionImports = new HashMap<>();
    private Map<String, Class<?>> complexTypes    = new HashMap<>();
    private Map<String, Class<?>> entityTypes     = new HashMap<>();

    private SaturnEdmContext context;

    public SaturnEdmProvider initialize() throws ODataApplicationException {
        ClassPathScanningCandidateComponentProvider provider = createComponentScanner(Arrays.asList(
                ODataAction.class,
                ODataActionImport.class,
                ODataComplexType.class,
                ODataEntitySet.class,
                ODataEnumType.class,
                ODataFunction.class,
                ODataFunctionImport.class));

        Set<BeanDefinition> beanDefinitions = provider.findCandidateComponents(context.getDefaultEdmPkg());

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

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
        Class<?> clazz = entityTypes.get(entityTypeName.getName());
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

    private ClassPathScanningCandidateComponentProvider createComponentScanner(Iterable<Class<? extends Annotation>> annotations) {
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        for (Class<? extends Annotation> annotation : annotations) {
            provider.addIncludeFilter(new AnnotationTypeFilter(annotation));
        }
        return provider;
    }
}
