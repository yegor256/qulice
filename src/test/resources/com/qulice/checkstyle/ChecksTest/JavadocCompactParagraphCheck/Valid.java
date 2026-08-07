/*
 * Hello.
 */
package com.qulice.checkstyle;

/**
 * This is not a real Java class. It won't be compiled ever.
 *
 * <p>The opening tag is glued to its text, and the closing tag is glued
 * to the text it closes.</p>
 */
public final class Valid {

    /**
     * A field.
     *
     * <p>Single paragraph without a closing tag.
     */
    private int number;

    /**
     * A method with a pre block that shows literal tags.
     *
     * <p>Example configuration:
     * <pre>
     * <p>
     * some example
     * </p>
     * </pre>
     *
     * @return Nothing
     */
    public int first() {
        return this.number;
    }

    /**
     * A method with a snippet that shows literal tags.
     *
     * <p>Here is a snippet:
     * {@snippet :
     * <p>
     * text
     * </p>
     * }
     *
     * @return Nothing
     */
    public int second() {
        return this.number;
    }
}
