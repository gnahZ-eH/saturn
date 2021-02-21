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

package com.github.saturn.example.operations;

import com.github.saturn.example.entities.Student;
import com.github.saturn.example.repository.StudentRepository;
import com.github.saturn.odata.interfaces.EntityOperation;
import com.github.saturn.odata.uri.QueryExpression;
import com.github.saturn.odata.uri.QueryExpressionFactory;
import com.github.saturn.odata.uri.QueryOptions;

import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class StudentOperation implements EntityOperation {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<?> retrieveAll(QueryOptions queryOptions, Object superObject) throws ODataApplicationException {
        QueryExpression queryExpression = new QueryExpressionFactory()
                .setClazz(Student.class)
                .setQueryOptions(queryOptions)
                .generate();
        return QueryExpression.launch(studentRepository, queryExpression);
    }

    @Override
    public String forEntity() {
        return "Student";
    }

    @Override
    public Object create(Object object, Object superObject) {
        return null;
    }

    @Override
    public Object retrieveByKey(Map<String, UriParameter> parameterMap, QueryOptions queryOptions, Object superObject) {
        UriParameter parameter = parameterMap.getOrDefault("Id", null);
        if (parameter == null) {
            throw new ServiceException("Entity Id should not be null.");
        }
        Optional<Student> student = studentRepository.findById(Integer.valueOf(parameter.getText()));
        return student.orElse(null);
    }

    @Override
    public Object update(Map<String, UriParameter> parameterMap, List<String> properties, Object object, Object superObject) {
        return null;
    }

    @Override
    public Object delete(Map<String, UriParameter> parameterMap, Object superObject) {
        return null;
    }

    @Override
    public Long count(QueryOptions queryOptions) {
        return null;
    }
}
