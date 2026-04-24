/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
package com.qulice.checkstyle;

/**
 * Class Javadoc with no text on the first line.
 */
public final class Valid {

    /**
     * Field Javadoc with no text on the first line.
     */
    private static final int X = 0;

    /**
     * Instance field Javadoc with no text on the first line.
     */
    private int y = 0;

    /**
     * Constructor Javadoc with no text on the first line.
     */
    public Valid() {}

    /**
     * Method Javadoc with no text on the first line.
     * @param param - param.
     * @return Param.
     */
    public String method(final String param) {
        return param;
    }

    /** Single line Javadoc is acceptable. */
    public void method2() {
    }

    /**
     **/
    public void method3() {
    }

    private static class InnerValid {

        /**
         * Inner field Javadoc with no text on the first line.
         */
        private final int x;

        /**
         * Inner constructor Javadoc with no text on the first line.
         * @param x - x
         */
        public InnerValid(int x) {
            this.x = x;
        }

        /**
         * Inner method Javadoc with no text on the first line.
         */
        public void method() {
        }
    }
}

/**
 * Interface Javadoc with no text on the first line.
 */
interface I1 {

    /**
     * Interface method Javadoc with no text on the first line.
     */
    void method();

    /**
     * Interface field Javadoc with no text on the first line.
     */
    int x = 0;

}

/**
 * Enum Javadoc with no text on the first line.
 */
enum ValidEnum {

    /**
     * Enum constant Javadoc with no text on the first line.
     */
    VALID_ENUM("someValue");

    /**
     * Enum field Javadoc with no text on the first line.
     */
    private final String name;

    /**
     * Enum constructor Javadoc with no text on the first line.
     */
    ValidEnum(final String name) {
        this.name = name;
    }
}

/**
 * Annotation Javadoc with no text on the first line.
 */
public @interface MyAnnotation {

    /**
     * Annotation field Javadoc with no text on the first line.
     * @return Return
     */
    int someValue();
}
