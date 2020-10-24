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

import com.github.saturn.odata.enums.PrimitiveType;
import com.github.saturn.odata.utils.StringUtils;

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.BooleanOperation;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.Member;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public abstract class ExpressionVisitor implements org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor<Expression<?>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Expression<?> visitBinaryOperator(BinaryOperatorKind binaryOperatorKind, Expression<?> lhs, Expression<?> rhs) throws ExpressionVisitException, ODataApplicationException {

        if (rhs == null && lhs instanceof Path<?>) {
            SimpleExpression<?> simpleExpression = (SimpleExpression<?>) lhs;

            if (binaryOperatorKind.equals(BinaryOperatorKind.EQ)) {
                return simpleExpression.isNull();
            } else {
                if (binaryOperatorKind.equals(BinaryOperatorKind.NE)) {
                    return simpleExpression.isNotNull();
                } else {
                    return null;
                }
            }
        }

        switch (binaryOperatorKind) {
            case GT:
            case GE:
            case LT:
            case LE:
            case EQ:
            case NE:
                if (rhs instanceof Constant<?>) {
                    Constant<?> constant = (Constant<?>) rhs;

                    if (constant.getType().isAssignableFrom(Integer.class)) {
                        NumberPath<Integer> numberPath = (NumberPath<Integer>) lhs;
                        Integer value = (Integer) constant.getConstant();

                        return binaryOperatorKind.equals(BinaryOperatorKind.GT) ? numberPath.gt(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.GE) ? numberPath.goe(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.LT) ? numberPath.lt(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.LE) ? numberPath.loe(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.EQ) ? numberPath.eq(value)
                                : numberPath.ne(value);

                    } else if (constant.getType().isAssignableFrom(Long.class)) {
                        NumberPath<Long> numberPath = (NumberPath<Long>) lhs;
                        Long value = (Long) constant.getConstant();

                        return binaryOperatorKind.equals(BinaryOperatorKind.GT) ? numberPath.gt(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.GE) ? numberPath.goe(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.LT) ? numberPath.lt(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.LE) ? numberPath.loe(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.EQ) ? numberPath.eq(value)
                                : numberPath.ne(value);
                    } else if (constant.getType().isAssignableFrom(Boolean.class)) {
                        BooleanPath booleanPath = (BooleanPath) lhs;
                        Boolean value = (Boolean) constant.getConstant();

                        return binaryOperatorKind.equals(BinaryOperatorKind.EQ) ? booleanPath.eq(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.NE) ? booleanPath.ne(value) : null;

                    } else if (constant.getType().isAssignableFrom(String.class)) {
                        StringPath stringPath = (StringPath) lhs;
                        String value = (String) constant.getConstant();

                        return binaryOperatorKind.equals(BinaryOperatorKind.GT) ? stringPath.gt(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.GE) ? stringPath.goe(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.LT) ? stringPath.lt(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.LE) ? stringPath.loe(value)
                                : binaryOperatorKind.equals(BinaryOperatorKind.EQ) ? stringPath.eq(value)
                                : stringPath.ne(value);
                    }
                    // todo
                }
                break;

            case HAS:
                if (rhs instanceof Constant<?>) {
                    Constant<?> constant = (Constant<?>) rhs;

                    if (Collection.class.isAssignableFrom(constant.getType())) {
                        List<Enum<?>> enums = (List<Enum<?>>) constant.getConstant();

                        if (lhs instanceof EnumPath<?>) {
                            EnumPath enumPath = (EnumPath) lhs;
                            return enumPath.in(enums);
                        }
                    }
                    // todo
                }
                break;

            case AND:
                return ((BooleanExpression) lhs).and((BooleanExpression) rhs);
            case OR:
                return ((BooleanExpression) lhs).or((BooleanExpression) rhs);
            default:
                break;
        }
        return null;
    }

    @Override
    public Expression<?> visitUnaryOperator(UnaryOperatorKind unaryOperatorKind, Expression<?> expression) throws ExpressionVisitException, ODataApplicationException {
        if (unaryOperatorKind.equals(UnaryOperatorKind.NOT)) {
            return ((BooleanOperation) expression).not();
        }
        return null;
    }

    @Override
    public Expression<?> visitMethodCall(MethodKind methodKind, List<Expression<?>> list) throws ExpressionVisitException, ODataApplicationException {
        return null;
    }

    @Override
    public Expression<?> visitLambdaExpression(String s, String s1, org.apache.olingo.server.api.uri.queryoption.expression.Expression expression) throws ExpressionVisitException, ODataApplicationException {
        return null;
    }

    @Override
    public Expression<?> visitLiteral(Literal literal) throws ExpressionVisitException, ODataApplicationException {

        String text = literal.getText();
        EdmType type = literal.getType();

        if (type == null) {
            return null;
        }

        switch (type.getName()) {
            case PrimitiveType.BYTE:
            case PrimitiveType.INT16:
            case PrimitiveType.INT32:
                return Expressions.constant(Integer.valueOf(text));
            case PrimitiveType.INT64:
                return Expressions.constant(Long.valueOf(text));
            case PrimitiveType.DOUBLE:
                return Expressions.constant(Double.valueOf(text));
            case PrimitiveType.DECIMAL:
                return Expressions.constant(BigDecimal.valueOf(Double.parseDouble(text)));
            case PrimitiveType.BOOLEAN:
                return Expressions.constant(Boolean.valueOf(text));
            case PrimitiveType.DATE:
                return Expressions.constant(LocalDate.parse(text));
            case PrimitiveType.DATE_TIME:
                return Expressions.constant(LocalDateTime.parse(text));
            default:
                return Expressions.constant(StringUtils.trimByChar(text, '\''));
        }
    }

    @Override
    public Expression<?> visitMember(Member member) throws ExpressionVisitException, ODataApplicationException {
        return null;
    }
}
