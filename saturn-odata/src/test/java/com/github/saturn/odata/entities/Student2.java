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

package com.github.saturn.odata.entities;

import com.github.saturn.odata.annotations.ODataEntityType;
import com.github.saturn.odata.annotations.ODataNavigationProperty;
import com.github.saturn.odata.utils.Constant;

import java.util.List;

@ODataEntityType(namespace = Constant.NAMESPACE, name = "Student2", keys = "Id")
public class Student2 {

    @ODataNavigationProperty(name = "Hobbies")
    private List<StudentHobby2> hobbies;

    @ODataNavigationProperty(name = "StudentAddress")
    private StudentAddress3 address;

    @ODataNavigationProperty
    private StudentAddress3 address2;

    @ODataNavigationProperty(name = "StudentAddress", type = "StudentAddress3")
    private StudentAddress3 address3;

    @ODataNavigationProperty(name = "StudentAddress3", partner = "Student2")
    private StudentAddress3 address4;
}
