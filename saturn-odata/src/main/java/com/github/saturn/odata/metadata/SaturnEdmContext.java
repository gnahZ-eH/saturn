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

public class SaturnEdmContext {

    private String NAME_SPACE = null;
    private String DEFAULT_EDM_PKG = null;
    private String CONTAINER_NAME = null;
    private String SERVICE_ROOT = null;

    public String getNameSpace() {
        return NAME_SPACE;
    }

    public void setNameSpace(String NAME_SPACE) {
        this.NAME_SPACE = NAME_SPACE;
    }

    public String getDefaultEdmPkg() {
        return DEFAULT_EDM_PKG;
    }

    public void setDefaultEdmPkg(String DEFAULT_EDM_PKG) {
        this.DEFAULT_EDM_PKG = DEFAULT_EDM_PKG;
    }

    public String getContainerName() {
        return CONTAINER_NAME;
    }

    public void setContainerName(String CONTAINER_NAME) {
        this.CONTAINER_NAME = CONTAINER_NAME;
    }

    public String getServiceRoot() {
        return SERVICE_ROOT;
    }

    public void setServiceRoot(String SERVICE_ROOT) {
        this.SERVICE_ROOT = SERVICE_ROOT;
    }
}
