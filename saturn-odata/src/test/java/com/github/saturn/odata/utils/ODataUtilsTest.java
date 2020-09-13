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

package com.github.saturn.odata.utils;

import com.github.saturn.odata.entities.Student;
import com.github.saturn.odata.entities.Student2;
import com.github.saturn.odata.entities.Student3;
import com.github.saturn.odata.entities.functions.GetName;
import com.github.saturn.odata.exceptions.SaturnODataException;

import com.github.saturn.odata.metadata.SaturnEdmContext;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.ex.ODataException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ODataUtilsTest {

    private static final SaturnEdmContext context = new SaturnEdmContext();

    @BeforeAll
    static void init() {
        context.setNameSpace(Constant.NAMESPACE);
        context.getFunctions().put("GetNameF", GetName.class);
        context.getEntitySets().put("Students", Student.class);
    }

    @Test
    void getCsdlPropertiesTest() {
        List<Field> fields = ClassUtils.getFields(Student.class);
        List<CsdlProperty> csdlProperties = ODataUtils.getCsdlProperties(fields, Constant.NAMESPACE);
        assertNotNull(csdlProperties);
    }

    @Test
    void getCsdlNavigationPropertiesTest() throws ODataException {
        List<Field> fields = ClassUtils.getFields(Student2.class);
        List<CsdlNavigationProperty> csdlNavigationProperties = ODataUtils.getCsdlNavigationProperties(fields, Constant.NAMESPACE);
        assertNotNull(csdlNavigationProperties);
    }

    @Test
    void getCsdlNavigationPropertiesTest2() throws ODataException {
        List<Field> fields = ClassUtils.getFields(Student3.class);
        Throwable exception = assertThrows(SaturnODataException.class, () -> {
            List<CsdlNavigationProperty> csdlNavigationProperties = ODataUtils.getCsdlNavigationProperties(fields, Constant.NAMESPACE);
        });
        assertNotNull(exception);
    }

    @Test
    void getCsdlNavigationPropertyBindings() throws ODataException {
        List<Field> fields = ClassUtils.getFields(Student2.class);
        List<CsdlNavigationPropertyBinding> csdlNavigationPropertyBindings = ODataUtils.getCsdlNavigationPropertyBindings(fields);
        assertNotNull(csdlNavigationPropertyBindings);
    }

    @Test
    void getCsdlNavigationPropertyBindings2() throws ODataException {
        List<Field> fields = ClassUtils.getFields(Student3.class);
        Throwable exception = assertThrows(SaturnODataException.class, () -> {
            List<CsdlNavigationPropertyBinding> csdlNavigationPropertyBindings = ODataUtils.getCsdlNavigationPropertyBindings(fields);
        });
        assertNotNull(exception);
    }

    @Test
    void getFunctionTest() {
        FullQualifiedName fullQualifiedName = new FullQualifiedName(Constant.NAMESPACE + ".functions", "GetNameF");
        FullQualifiedName fullQualifiedName2 = new FullQualifiedName(Constant.NAMESPACE + ".functions", "GetNameF2");

        CsdlFunction csdlFunction = ODataUtils.getFunction(fullQualifiedName, context);
        CsdlFunction csdlFunction2 = ODataUtils.getFunction(fullQualifiedName2, context);

        assertNotNull(csdlFunction);
        assertNull(csdlFunction2);
    }

    @Test
    void generateFormatedEntityIdTest() {
        Map<String, Object> keyValues = new HashMap<>();
        keyValues.put("Id", 2);
        keyValues.put("ClassId", 3);
        keyValues.put("Name", "<joe zhang>");
        String res = ODataUtils.generateFormatedEntityId(keyValues);
        assertNotNull(res);
    }
}