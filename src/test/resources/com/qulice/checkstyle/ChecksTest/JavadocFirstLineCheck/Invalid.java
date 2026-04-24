/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
package com.qulice.checkstyle;

/** Class Javadoc with text on the first line.
 */
public final class Invalid {

    /** Field Javadoc with text on the first line.
     */
    private static final int X = 0;

    /** Instance field Javadoc with text on the first line.
     */
    private int y = 0;

    /** Constructor Javadoc with text on the first line.
     */
    public Invalid() {}

    /** Method Javadoc with text on the first line.
     * @param param - param.
     * @return Param.
     */
    public String method(final String param) {
        return param;
    }

    /** Void method Javadoc with text on the first line.
     */
    public void method2() {
    }

    private static class InnerInvalid {

        /** Inner field Javadoc with text on the first line.
         */
        private final int x;

        /** Inner constructor Javadoc with text on the first line.
         * @param x - x
         */
        public InnerInvalid(int x) {
            this.x = x;
        }

        /** Inner method Javadoc with text on the first line.
         */
        public void method() {
        }
    }
}

/** Interface Javadoc with text on the first line.
 */
interface I1 {

    /** Interface method Javadoc with text on the first line.
     */
    void method();

    /** Interface field Javadoc with text on the first line.
     */
    int x = 0;

}

/** Enum Javadoc with text on the first line.
 */
enum InvalidEnum {

    /** Enum constant Javadoc with text on the first line.
     */
    INVALID_ENUM("someValue");

    /** Enum field Javadoc with text on the first line.
     */
    private final String name;

    /** Enum constructor Javadoc with text on the first line.
     */
    InvalidEnum(final String name) {
        this.name = name;
    }
}

/** Annotation Javadoc with text on the first line.
 */
public @interface MyAnnotation {

    /** Annotation field Javadoc with text on the first line.
     * @return Return
     */
    int someValue();
}
