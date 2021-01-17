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

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Arrays;
import java.util.List;

public class QueryExpression {

    private Pageable pageable = null;
    private BooleanExpression booleanExpression = null;
    private OrderSpecifier<?>[] orderSpecifiers = null;

    public QueryExpression(OrderSpecifier<?>[] orderSpecifiers, BooleanExpression booleanExpression) {
        this.orderSpecifiers = orderSpecifiers;
        this.booleanExpression = booleanExpression;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public QueryExpression setPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }

    public BooleanExpression getBooleanExpression() {
        return booleanExpression;
    }

    public OrderSpecifier<?>[] getOrderSpecifiers() {
        return orderSpecifiers;
    }

    @Override
    public String toString() {
        return "QueryExpression [BooleanExpression = " + booleanExpression + ", OrderSpecifiers = " + Arrays.toString(orderSpecifiers) + "]";
    }


    public static <T> List<T> launch(QuerydslPredicateExecutor<T> queryDslPredicateExecutor, QueryExpression queryExpression) {
        List<T> res;
        if (queryExpression.getPageable() == null) {
            if (queryExpression.getOrderSpecifiers() == null) {
                res = (List<T>) queryDslPredicateExecutor.findAll(queryExpression.getBooleanExpression());
            } else {
                res = (List<T>) queryDslPredicateExecutor.findAll(queryExpression.getBooleanExpression(), queryExpression.getOrderSpecifiers());
            }
        } else {
            Page<T> page = queryDslPredicateExecutor.findAll(queryExpression.getBooleanExpression(), queryExpression.getPageable());
            res = page.getContent();
        }
        return res;
    }
}
