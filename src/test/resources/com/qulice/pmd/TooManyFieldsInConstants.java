/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class TooManyFieldsInConstants {

    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final int SIX = 6;
    private static final int SEVEN = 7;
    private static final int EIGHT = 8;
    private static final int NINE = 9;
    private static final int TEN = 10;
    private static final int ELEVEN = 11;
    private static final int TWELVE = 12;
    private static final int THIRTEEN = 13;
    private static final int FOURTEEN = 14;
    private static final int FIFTEEN = 15;
    private static final int SIXTEEN = 16;

    public int total() {
        return TooManyFieldsInConstants.ONE + TooManyFieldsInConstants.TWO
            + TooManyFieldsInConstants.THREE + TooManyFieldsInConstants.FOUR
            + TooManyFieldsInConstants.FIVE + TooManyFieldsInConstants.SIX
            + TooManyFieldsInConstants.SEVEN + TooManyFieldsInConstants.EIGHT
            + TooManyFieldsInConstants.NINE + TooManyFieldsInConstants.TEN
            + TooManyFieldsInConstants.ELEVEN + TooManyFieldsInConstants.TWELVE
            + TooManyFieldsInConstants.THIRTEEN
            + TooManyFieldsInConstants.FOURTEEN
            + TooManyFieldsInConstants.FIFTEEN
            + TooManyFieldsInConstants.SIXTEEN;
    }
}
