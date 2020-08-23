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

import com.github.saturn.odata.entities.Man;
import com.github.saturn.odata.entities.Person;
import com.github.saturn.odata.entities.Woman;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ClassUtilsTest {

    @Test
    void getFieldsTest() {
        Person person = new Person();
        Man man = new Man();
        Woman woman = new Woman();

        List<Field> fields = ClassUtils.getFields(Man.class); // with super class
        List<Field> fields1 = ClassUtils.getFields(Man.class, false);
        List<Field> fields2 = ClassUtils.getFields(Woman.class, true);

        Set<Field> actualManFields = Arrays.stream(man.getClass().getDeclaredFields()).collect(Collectors.toSet());
        Set<Field> actualPersonFields = Arrays.stream(person.getClass().getDeclaredFields()).collect(Collectors.toSet());
        Set<Field> actualWomanFields = Arrays.stream(woman.getClass().getDeclaredFields()).collect(Collectors.toSet());

        Set<Field> actualFields = new HashSet<>();
        actualFields.addAll(actualManFields);
        actualFields.addAll(actualPersonFields);

        assertEquals(fields.size(), actualFields.size());
        assertEquals(fields1.size(), actualManFields.size());
        assertEquals(fields2.size(), actualWomanFields.size());

        for (Field field : fields) {
            assertTrue(actualFields.contains(field));
        }

        for (Field field : fields1) {
            assertTrue(actualManFields.contains(field));
        }

        for (Field field : fields2) {
            assertTrue(actualWomanFields.contains(field));
        }
    }
}