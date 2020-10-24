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

public final class StringUtils extends org.apache.commons.lang.StringUtils {

    private StringUtils() { }

    public static final String COLLECTION_QUALIFIED_NAME = "Collection(%s.%s)";

    public static final String COLLECTION = "Collection";

    public static final String COLLECTION_QUALIFIED_FULL_NAME = "Collection(%s)";

    public static final String FQN = "%s.%s";

    //--------------------------------- REG --------------------------------------------------
    public static final String REGEX_DATE_FORMAT = "\\d{4}-\\d{2}-\\d{2}";

    //--------------------------------- BASE-SYMBOL ------------------------------------------
    public static final String COMMA = ",";
    public static final String LEFT_BRACKET = "(";
    public static final String RIGHT_BRACKET = ")";
    public static final String LEFT_ANGLE_BRACKET = "<";
    public static final String RIGHT_ANGLE_BRACKET = ">";
    public static final String DOLLAR = "$";
    public static final String BLANK = " ";
    public static final String QUESTION_MARK = "?";

    //--------------------------------- FOR-URL-ENCODE ---------------------------------------
    public static final String BLANK_CODE = "%20";
    public static final String LEFT_ANGLE_BRACKET_CODE = "%3C";
    public static final String RIGHT_ANGLE_BRACKET_CODE = "%3E";
    public static final String DOLLAR_CODE = "%24";

    //--------------------------------- FOR-URI-QUERY-PARAMS ---------------------------------
    public static final String AND = "&";
    public static final String EQ = "=";
    public static final String COUNT = DOLLAR + "count";
    public static final String COUNT_URL = DOLLAR_CODE + "count";
    public static final String SKIP = DOLLAR + "skip";
    public static final String SKIP_URL = DOLLAR_CODE + "skip";

    //--------------------------------- BOOLEAN ----------------------------------------------
    public static final String TRUE = "true";

    //--------------------------------- METHODS ----------------------------------------------
    public static boolean equals(final String s1, final String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.equals(s2);
    }

    public static boolean isNotNull(final String s) {
        return s != null;
    }

    public static boolean isNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    public static String trimByChar(String text, char c) {
        int len = text.length();

        int first = 0;
        while (first < len && text.charAt(first) == c) {
            first++;
        }

        int last = len - 1;

        while (last > first && text.charAt(last) == c) {
            last--;
        }

        return text.substring(first, last + 1);
    }
}
