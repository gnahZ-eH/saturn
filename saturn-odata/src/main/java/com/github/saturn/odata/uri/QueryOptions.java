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

package com.github.saturn.odata.uri;

import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

import java.util.Optional;

public class QueryOptions {

    private Optional<ExpandOption> expandOption;
    private Optional<FilterOption> filterOption;
    private Optional<SelectOption> selectOption;
    private Optional<OrderByOption> orderByOption;

    private int skip = 0;
    private int top = Integer.MAX_VALUE;

    public QueryOptions(final ExpandOption expandOption, final FilterOption filterOption, final SelectOption selectOption, final OrderByOption orderByOption) {
        super();
        if (expandOption != null) {
            this.expandOption = Optional.of(expandOption);
        }
        if (filterOption != null) {
            this.filterOption = Optional.of(filterOption);
        }
        if (selectOption != null) {
            this.selectOption = Optional.of(selectOption);
        }
        if (orderByOption != null) {
            this.orderByOption = Optional.of(orderByOption);
        }
    }

    public Optional<ExpandOption> getExpandOption() {
        return expandOption;
    }

    public void setExpandOption(Optional<ExpandOption> expandOption) {
        this.expandOption = expandOption;
    }

    public Optional<FilterOption> getFilterOption() {
        return filterOption;
    }

    public void setFilterOption(Optional<FilterOption> filterOption) {
        this.filterOption = filterOption;
    }

    public Optional<SelectOption> getSelectOption() {
        return selectOption;
    }

    public void setSelectOption(Optional<SelectOption> selectOption) {
        this.selectOption = selectOption;
    }

    public Optional<OrderByOption> getOrderByOption() {
        return orderByOption;
    }

    public void setOrderByOption(Optional<OrderByOption> orderByOption) {
        this.orderByOption = orderByOption;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }
}
