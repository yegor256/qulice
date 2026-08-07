/*
 * Hello.
 */
package com.qulice.checkstyle;

/**
 * This is not a real Java class. It won't be compiled ever.
 *
 * <p>
 * Text after a lonely opening tag.
 * </p>
 */
public final class Invalid {

    /**
     * A field.
     *
     * <p>Some paragraph here.
     * </p>
     */
    private int number;

    /**
     * A method.
     *
     * <p>
     * Another paragraph with a lonely opening tag.
     *
     * @return Nothing
     */
    public int method() {
        return this.number;
    }
}
