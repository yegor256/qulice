/**
 * There is no empty Javadoc line at the beginning
 */
package com.qulice.checkstyle;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {

    /**
     * There is no empty Javadoc line at the beginning.
     */
    private static final int x = 0;

    /**
     * There is no empty Javadoc line at the beginning.
     */
    private int y = 0;

    /**
     * There is no empty Javadoc line at the end.
     */
    private int z = 0;

    /**
     * There is no empty Javadoc line at the beginning.
     */
    public Valid() {}

    /**
     * There is no empty Javadoc line
     * @param param - param.
     * @return Param.
     */
    public String method(final String param) {
        return param;
    }

    /*
     * It's not Javadoc, just some comment
     *
     */
    public void method2() {
    }

    /**
     * First.
     *
     * Second.
     */
    public void method3() {
    }

    /** Some text
     *
     */
    public void method4() {

    }

    private static class InnerValid {

        /**
         * There is no empty Javadoc line at the end.
         */
        private final int x;

        /**
         * There is no empty Javadoc line at the beginning.
         * @param x - x
         */
        public InnerValid(int x) {
            this.x = x;
        }

        /**
         * There is no empty Javadoc line at the beginning.
         */
        public void method() {
        }
    }
}

/**
 * This is not a real interface. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
interface I1 {

    /**
     * There is no empty Javadoc line at the beginning
     */
    void method();

    /**
     * Integer variable.
     */
    int x = 0;

}

/**
 * There is no empty Javadoc line.
 */
enum ValidEnum {

    /**
     * There is no empty Javadoc line at the beginning.
     */
    VALID_ENUM("someValue");

    /**
     * There is no empty Javadoc line at the end.
     */
    private final String name;

    /**
     * There is no empty Javadoc line at the end.
     */
    ValidEnum(final String name) {
        this.name = name;
    }
}

/**
 * There is no empty Javadoc line.
 */
public @interface MyAnnotation {

    /**
     * There is no empty Javadoc line.
     * @return Return
     */
    int someValue();
}