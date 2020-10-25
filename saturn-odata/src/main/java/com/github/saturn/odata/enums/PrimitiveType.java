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

package com.github.saturn.odata.enums;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public enum PrimitiveType {

    EDM_BYTE      ("Edm.SByte"),
    EDM_INT16     ("Edm.Int16"),
    EDM_INT32     ("Edm.Int32"),
    EDM_INT64     ("Edm.Int64"),
    EDM_STRING    ("Edm.String"),
    EDM_DATE      ("Edm.Date"),
    EDM_DECIMAL   ("Edm.Decimal"),
    EDM_DOUBLE    ("Edm.Double"),
    EDM_BOOLEAN   ("Edm.Boolean"),
    EDM_DATE_TIME ("Edm.DateTimeOffset");

    private final String type;
    public static final Map<String, EdmPrimitiveTypeKind> EDM_PT_BY_NAME = new HashMap<>();
    public static final Map<EdmPrimitiveTypeKind, PrimitiveType> PT_BY_EDM_PT = new HashMap<>();
    public static final Map<String, Class<?>> BT_BY_PT_NAME = new HashMap<>();
    public static final Map<Class<?>, PrimitiveType> PT_BY_BT = new HashMap<>();

    public static final String BYTE      = "SByte";
    public static final String INT16     = "Int16";
    public static final String INT32     = "Int32";
    public static final String INT64     = "Int64";
    public static final String STRING    = "String";
    public static final String DATE      = "Date";
    public static final String DECIMAL   = "Decimal";
    public static final String DOUBLE    = "Double";
    public static final String BOOLEAN   = "Boolean";
    public static final String DATE_TIME = "DateTimeOffset";

    static {
        EDM_PT_BY_NAME.put(EDM_BYTE.type,      EdmPrimitiveTypeKind.SByte);
        EDM_PT_BY_NAME.put(EDM_INT16.type,     EdmPrimitiveTypeKind.Int16);
        EDM_PT_BY_NAME.put(EDM_INT32.type,     EdmPrimitiveTypeKind.Int32);
        EDM_PT_BY_NAME.put(EDM_INT64.type,     EdmPrimitiveTypeKind.Int64);
        EDM_PT_BY_NAME.put(EDM_STRING.type,    EdmPrimitiveTypeKind.String);
        EDM_PT_BY_NAME.put(EDM_DATE.type,      EdmPrimitiveTypeKind.Date);
        EDM_PT_BY_NAME.put(EDM_DECIMAL.type,   EdmPrimitiveTypeKind.Decimal);
        EDM_PT_BY_NAME.put(EDM_DOUBLE.type,    EdmPrimitiveTypeKind.Double);
        EDM_PT_BY_NAME.put(EDM_BOOLEAN.type,   EdmPrimitiveTypeKind.Boolean);
        EDM_PT_BY_NAME.put(EDM_DATE_TIME.type, EdmPrimitiveTypeKind.DateTimeOffset);

        PT_BY_EDM_PT.put(EdmPrimitiveTypeKind.SByte,          EDM_BYTE);
        PT_BY_EDM_PT.put(EdmPrimitiveTypeKind.Int16,          EDM_INT16);
        PT_BY_EDM_PT.put(EdmPrimitiveTypeKind.Int32,          EDM_INT32);
        PT_BY_EDM_PT.put(EdmPrimitiveTypeKind.Int64,          EDM_INT64);
        PT_BY_EDM_PT.put(EdmPrimitiveTypeKind.String,         EDM_STRING);
        PT_BY_EDM_PT.put(EdmPrimitiveTypeKind.Date,           EDM_DATE);
        PT_BY_EDM_PT.put(EdmPrimitiveTypeKind.Decimal,        EDM_DECIMAL);
        PT_BY_EDM_PT.put(EdmPrimitiveTypeKind.Double,         EDM_DOUBLE);
        PT_BY_EDM_PT.put(EdmPrimitiveTypeKind.Boolean,        EDM_BOOLEAN);
        PT_BY_EDM_PT.put(EdmPrimitiveTypeKind.DateTimeOffset, EDM_DATE_TIME);

        BT_BY_PT_NAME.put(EDM_BYTE.type,      Byte.class);
        BT_BY_PT_NAME.put(EDM_INT16.type,     Short.class);
        BT_BY_PT_NAME.put(EDM_INT32.type,     Integer.class);
        BT_BY_PT_NAME.put(EDM_INT64.type,     Long.class);
        BT_BY_PT_NAME.put(EDM_STRING.type,    String.class);
        BT_BY_PT_NAME.put(EDM_DATE.type,      LocalDate.class);
        BT_BY_PT_NAME.put(EDM_DECIMAL.type,   BigDecimal.class);
        BT_BY_PT_NAME.put(EDM_DOUBLE.type,    Double.class);
        BT_BY_PT_NAME.put(EDM_BOOLEAN.type,   Boolean.class);
        BT_BY_PT_NAME.put(EDM_DATE_TIME.type, LocalDateTime.class);

        PT_BY_BT.put(Byte.class,          EDM_BYTE);
        PT_BY_BT.put(byte.class,          EDM_BYTE);
        PT_BY_BT.put(Short.class,         EDM_INT16);
        PT_BY_BT.put(short.class,         EDM_INT16);
        PT_BY_BT.put(Integer.class,       EDM_INT32);
        PT_BY_BT.put(int.class,           EDM_INT32);
        PT_BY_BT.put(Long.class,          EDM_INT64);
        PT_BY_BT.put(long.class,          EDM_INT64);
        PT_BY_BT.put(String.class,        EDM_STRING);
        PT_BY_BT.put(LocalDate.class,     EDM_DATE);
        PT_BY_BT.put(BigDecimal.class,    EDM_DECIMAL);
        PT_BY_BT.put(Double.class,        EDM_DOUBLE);
        PT_BY_BT.put(double.class,        EDM_DOUBLE);
        PT_BY_BT.put(Float.class,         EDM_DOUBLE);
        PT_BY_BT.put(float.class,         EDM_DOUBLE);
        PT_BY_BT.put(Boolean.class,       EDM_BOOLEAN);
        PT_BY_BT.put(boolean.class,       EDM_DOUBLE);
        PT_BY_BT.put(LocalDateTime.class, EDM_DATE_TIME);
    }

    PrimitiveType(final String t) {
        this.type = t;
    }

    public String getType() {
        return type;
    }

    public String getBaseType() {
        return type.split("\\.")[1];
    }
}
