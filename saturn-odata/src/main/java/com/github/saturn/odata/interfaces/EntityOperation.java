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

package com.github.saturn.odata.interfaces;

import com.github.saturn.odata.uri.QueryOptions;

import org.apache.olingo.server.api.uri.UriParameter;

import java.util.List;
import java.util.Map;

public interface EntityOperation {

    String forEntity();

    /**
     *
     * @param object       edm defined by selves.
     * @param superObject
     * @return             edm defined by selves.
     */
    Object create(Object object, Object superObject);

    Object retrieveByKey(Map<String, UriParameter> parameterMap, QueryOptions queryOptions, Object superObject);

    default Object retrieveByKey(Map<String, UriParameter> parameterMap) {
        return retrieveByKey(parameterMap, null, null);
    }

    List<?> retrieveAll(QueryOptions queryOptions, Object superObject);

    Object update(Map<String, UriParameter> parameterMap, List<String> properties, Object object, Object superObject);

    Object delete(Map<String, UriParameter> parameterMap, Object superObject);

    Long count(QueryOptions queryOptions);
}
