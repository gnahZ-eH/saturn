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

package com.github.saturn.example.configs;

import com.github.saturn.odata.metadata.SaturnEdmContext;
import com.github.saturn.odata.metadata.SaturnEdmProvider;
import com.github.saturn.odata.processors.EntityProcessor;
import com.github.saturn.odata.processors.PrimitiveProcessor;
import com.github.saturn.example.controllers.SaturnServlet;

import org.apache.olingo.server.api.ODataApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaturnConfig implements WebMvcConfigurer {

    @Value("${saturn.service-root}")
    private String SERVICE_ROOT;

    @Value("${saturn.name-space}")
    private String NAME_SPACE;

    @Value("${saturn.container-name}")
    private String CONTAINER_NAME;

    @Value("${saturn.default-package}")
    private String DEFAULT_PACKAGE;

    @Value("100")
    private Integer TOP_MAX_VALUE;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SaturnEdmContext saturnEdmContext;

    @Autowired
    private SaturnServlet dispatcherServlet;

    @Bean
    public PrimitiveProcessor getPrimitiveProcessor() throws ODataApplicationException {
        return new PrimitiveProcessor()
                .initialize(saturnEdmContext, applicationContext);
    }

    @Bean
    public SaturnEdmContext getSaturnEdmContext() throws ODataApplicationException {
        return new SaturnEdmContext()
                .setDefaultEdmPkg(DEFAULT_PACKAGE)
                .setContainerName(CONTAINER_NAME)
                .setNameSpace(NAME_SPACE)
                .setServiceRoot(SERVICE_ROOT)
                .setTopMaxValue(TOP_MAX_VALUE)
                .initialize();
    }

    @Bean
    public SaturnEdmProvider getSaturnEdmProvider() throws ODataApplicationException {
        return new SaturnEdmProvider()
                .initialize(saturnEdmContext);
    }

    @Bean
    public EntityProcessor getEntityProcessor() {
        return new EntityProcessor()
                .initialize(saturnEdmContext, applicationContext);
    }

    @Bean
    public ServletRegistrationBean<SaturnServlet> servletRegistrationBean() {
        return new ServletRegistrationBean<>(dispatcherServlet, "/saturn-odata/*");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseRegisteredSuffixPatternMatch(true);
    }
}
