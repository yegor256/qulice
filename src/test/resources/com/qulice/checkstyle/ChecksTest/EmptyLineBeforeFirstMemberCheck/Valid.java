/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
package com.qulice.checkstyle;

public final class Valid {

    /**
     * Field.
     */
    private static final String FIELD = "field";

    /**
     * Inner class.
     */
    private static final class Inner {

        private final int value;

        Inner(final int val) {
            this.value = val;
        }
    }
}

interface I1 {

    void method();
}

enum ValidEnum {

    RED,
    GREEN,
    BLUE;
}

final class EmptyClass {
}

final class EmptyClassBlank {

}
