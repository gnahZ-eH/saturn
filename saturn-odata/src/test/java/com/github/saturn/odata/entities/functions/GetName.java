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

package com.github.saturn.odata.entities.functions;

import com.github.saturn.odata.annotations.ODataFunction;
import com.github.saturn.odata.annotations.ODataParameter;
import com.github.saturn.odata.annotations.ODataReturnType;
import com.github.saturn.odata.entities.enums.Sex;
import com.github.saturn.odata.entities.enums.Sex2;
import com.github.saturn.odata.utils.Constant;

@ODataFunction(name = "GetNameF", namespace = Constant.NAMESPACE + ".functions", isBound = true, entitySetPath = "Students")
@ODataReturnType(type = "Edm.String")
public class GetName {

    @ODataParameter(name = "name", type = "Edm.String")
    private String name;

    @ODataParameter(name = "name2")
    private String name2;

    @ODataParameter
    private String name3;

    @ODataParameter(name = "sex")
    private Sex sex;

    @ODataParameter(name = "sex2")
    private Sex2 sex2;

    @ODataParameter(name = "sex3", type = "Sex")
    private Sex sex3;
}
