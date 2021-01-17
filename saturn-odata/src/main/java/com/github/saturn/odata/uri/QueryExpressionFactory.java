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

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class QueryExpressionFactory {

    private FilterOption filterOption = null;
    private QueryOptions queryOptions = null;
    private OrderByOption orderByOption = null;
    private Class<?> clazz = null;

    public QueryExpression generate() throws ODataApplicationException {

        BooleanExpression booleanExpression = generateBooleanExpression();
        OrderSpecifier<?>[] orderSpecifiers = generateOrderSpecifiers();
        QueryExpression queryExpression = new QueryExpression(orderSpecifiers, booleanExpression);

        if (queryOptions != null && (!queryOptions.isDefaultSkip() || !queryOptions.isDefaultTop())) {
            List<Order> orders = orderSpecifiers == null ? null : Arrays.stream(orderSpecifiers)
                .map(orderSpecifier -> {
                    com.querydsl.core.types.Expression<?> target = orderSpecifier.getTarget();
                    Object targetElement = target instanceof Path ? preparePropertyPath((Path<?>) target) : target;
                    return Order.by(targetElement.toString()).with(orderSpecifier.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC);
                })
                .collect(Collectors.toList());

            PageRequest pageRequest = PageRequest.of(
                queryOptions.isDefaultSkip() ? 0 : queryOptions.getSkip(),
                queryOptions.isDefaultTop() ? Integer.MAX_VALUE : queryOptions.getTop(),
                orders == null ? Sort.unsorted() : Sort.by(orders));

            queryExpression.setPageable(pageRequest);
        }
        return queryExpression;
    }

    private BooleanExpression generateBooleanExpression() throws ODataApplicationException {
        if (filterOption != null) {
            Expression filterExpression = filterOption.getExpression();
            QueryExpressionVisitor visitor = new QueryExpressionVisitor(clazz);

            try {
                com.querydsl.core.types.Expression<?> expression = filterExpression.accept(visitor);
                return (BooleanExpression) expression;

            } catch (ExpressionVisitException | ODataApplicationException e) {
                throw new ODataApplicationException(e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
            }
        }
        return (BooleanExpression) new BooleanBuilder().getValue();
    }

    private OrderSpecifier<?>[] generateOrderSpecifiers() throws ODataApplicationException {
        if (orderByOption != null) {
            List<OrderByItem> orderByItems = orderByOption.getOrders();
            List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

            for (OrderByItem item : orderByItems) {
                Expression orderExpression = item.getExpression();
                boolean descendOrder = item.isDescending();
                QueryExpressionVisitor visitor = new QueryExpressionVisitor(clazz);

                try {
                    com.querydsl.core.types.Expression<?> expression = orderExpression.accept(visitor);

                    if (expression instanceof ComparableExpressionBase<?>) {
                        ComparableExpressionBase<?> comparableExpression = (ComparableExpressionBase<?>) expression;
                        orderSpecifiers.add(descendOrder ? comparableExpression.desc() : comparableExpression.asc());
                    }
                } catch (ExpressionVisitException | ODataApplicationException e) {
                    throw new ODataApplicationException(e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
                }
            }
            return orderSpecifiers.toArray(new OrderSpecifier<?>[0]);
        }
        return null;
    }

    private QueryExpressionFactory setQueryOptions(QueryOptions queryOptions) {
        this.queryOptions = queryOptions;

        if (queryOptions != null) {
            queryOptions.getFilterOption().ifPresent(option -> this.filterOption = option);
            queryOptions.getOrderByOption().ifPresent(option -> this.orderByOption = option);
        }
        return this;
    }

    private String preparePropertyPath(Path<?> path) {
        Path<?> root = path.getRoot();
        return root == null || path.equals(root) ? path.toString()
                : path.toString().substring(root.toString().length() + 1);
    }

    public QueryExpressionFactory setClazz(Class<?> clazz) {
        this.clazz = clazz;
        return this;
    }
}
