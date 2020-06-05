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

import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SaturnEdmProvider extends CsdlAbstractEdmProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SaturnEdmProvider.class);

    private Map<String, Class<?>> entitiesMap        = new HashMap<>();
    private Map<String, Class<?>> enumsMap           = new HashMap<>();
    private Map<String, Class<?>> actionsMap         = new HashMap<>();
    private Map<String, Class<?>> actionImportsMap   = new HashMap<>();
    private Map<String, Class<?>> functionsMap       = new HashMap<>();
    private Map<String, Class<?>> functionImportsMap = new HashMap<>();
    private Map<String, Class<?>> complexTypesMap    = new HashMap<>();
    private Map<String, String>   entityTypesMap     = new HashMap<>();

    private String NAME_SPACE = null;
    private String DEFAULT_EDM_PKG = null;



}
