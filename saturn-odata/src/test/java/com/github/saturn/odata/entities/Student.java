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
import com.github.saturn.odata.annotations.ODataProperty;
import com.github.saturn.odata.entities.enums.Sex;
import com.github.saturn.odata.entities.enums.Sex2;
import com.github.saturn.odata.utils.Constant;

import java.util.List;

@ODataEntityType(namespace = Constant.NAMESPACE, name = "Student", keys = "Id")
public class Student {

    @ODataProperty(name = "Id")
    private long id;

    @ODataProperty(name = "Name")
    private String name;

    @ODataProperty(name = "Birth")
    private int birth;

    @ODataProperty(name = "Hobbies")
    private List<StudentHobby> hobbies;

    @ODataProperty(name = "StudentAddress")
    private StudentAddress address;

    @ODataProperty(name = "StudentAddress2")
    private StudentAddress2 address2;

    @ODataProperty
    private boolean married;

    @ODataProperty(name = "Sex")
    private Sex sex;

    @ODataProperty(name = "ClassNo", type = "Edm.Int32")
    private int classNo;

    @ODataProperty(name = "Points")
    private List<Integer> points;

    @ODataProperty(name = "Sex2")
    private Sex2 sex2;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirth() {
        return birth;
    }

    public void setBirth(int birth) {
        this.birth = birth;
    }

    public List<StudentHobby> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<StudentHobby> hobbies) {
        this.hobbies = hobbies;
    }

    @Override
    public String toString() {
        return "UserEntity -> [id = " + id + ", name = " + name + "]";
    }
}
