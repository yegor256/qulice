/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
package com.qulice.checkstyle;

public final class Invalid {
    /**
     * Field.
     */
    private static final String FIELD = "field";

    private static final class Inner {
        private final int value;

        Inner(final int val) {
            this.value = val;
        }
    }
}

interface InvalidIface {
    void method();
}

enum InvalidEnum {
    RED,
    GREEN,
    BLUE;
}
