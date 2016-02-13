/**
 *
 * Empty javadoc line at the beginning.
 */
package com.qulice.checkstyle;

/**
 *
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {

    /**
     *
     * Empty javadoc line at the beginning.
     */
    private static final int x = 0;

    /**
     *
     * Empty javadoc line at the beginning.
     */
    private int y = 0;

    /**
     * Empty javadoc line at the end.
     *
     */
    private int z = 0;

    /**
     *
     * Empty javadoc line at the beginning.
     */
    public Invalid() {}

    /**
     *
     * Empty javadoc line
     * @param param - param.
     * @return Param.
     *
     */
    public String method(final String param) {
        return param;
    }

    /**
     *
     * Empty javadoc line at the beginning.
     */
    public void method2() {
    }

    /**
     *
     * Empty javadoc line at the beginning.
     */

    public void method3() {
    }

    private static class InnerInvalid {

        /**
         * Empty javadoc line at the end.
         *
         */
        private final int x;

        /**
         *
         * Empty javadoc line at the beginning.
         * @param x - x
         */
        public InnerInvalid(int x) {
            this.x = x;
        }

        /**
         *
         * Empty javadoc line at the beginning.
         */
        public void method() {
        }
    }
}

/**
 * This is not a real interface class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 *
 */
interface I1 {

    /**
     *
     * Empty javadoc line at the beginning
     */
    void method();

    /**
     *
     * Integer variable.
     */
    int x = 0;

}

/**
 *
 * Empty javadoc line.
 *
 */
enum InvalidEnum {

    /**
     *
     * Empty javadoc line at the beginning.
     */
    INVALID_ENUM("someValue");

    /**
     * Empty javadoc line at the end.
     *
     */
    private final String name;

    /**
     * Empty javadoc line at the end.
     *
     */
    InvalidEnum(final String name) {
        this.name = name;
    }
}

/**
 * Empty javadoc line at the end.
 *
 */
public @interface MyAnnotation {

    /**
     *
     * Empty javadoc line at the beginning.
     * @return Return
     */
    int someValue();
}