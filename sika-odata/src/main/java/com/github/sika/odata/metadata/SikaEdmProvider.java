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

package com.github.sika.odata.metadata;


import java.lang.annotation.Annotation;
import java.util.*;

import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sika.odata.annotations.*;

public class SikaEdmProvider extends CsdlAbstractEdmProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SikaEdmProvider.class);

    private Map<String, Class<?>> entitySets      = new HashMap<>();
    private Map<String, Class<?>> enums           = new HashMap<>();
    private Map<String, Class<?>> actions         = new HashMap<>();
    private Map<String, Class<?>> actionImports   = new HashMap<>();
    private Map<String, Class<?>> functions       = new HashMap<>();
    private Map<String, Class<?>> functionImports = new HashMap<>();
    private Map<String, Class<?>> complexTypes    = new HashMap<>();
    private Map<String, String>   entityTypes     = new HashMap<>();

    private String NAME_SPACE = null;
    private String DEFAULT_EDM_PKG = null;
    private String CONTAINER_NAME = null;
    private String SERVICE_ROOT = null;

    public SikaEdmProvider initialize() throws ODataApplicationException {
        ClassPathScanningCandidateComponentProvider provider = createComponentScanner(Arrays.asList(
                ODataAction.class,
                ODataActionImport.class,
                ODataComplexType.class,
                ODataEntitySet.class,
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
            }

            if (complexType != null) {
                // todo
            }
        }
        return null;
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
