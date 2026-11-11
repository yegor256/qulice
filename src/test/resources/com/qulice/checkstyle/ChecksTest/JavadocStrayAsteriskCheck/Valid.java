/*
 * Hello.
 */
package com.qulice.checkstyle;

/**
 * This is not a real Java class. It won't be compiled ever.
 *
 * <p>No line of this comment ends with an asterisk of its own, only the
 * comment itself is delimited by them.</p>
 */
public final class Valid {

    /**
     * A field.
     *
     * <p>A paragraph that is not closed at all is fine here.
     */
    private int number;

    /**
     * A method whose text mentions an asterisk, without ending on one.
     *
     * <p>The wildcard is written as {@code *}
     * and the multiplication sign is <b>*</b> here.
     *
     * @return Nothing
     */
    public int wildcard() {
        return this.number;
    }

    /**
     * A method with a pre block that shows a literal asterisk.
     *
     * <p>Example:
     * <pre>
     * int total = first *
     *     second;
     * </pre>
     *
     * @return Nothing
     */
    public int first() {
        return this.number;
    }

    /**
     * A method with a snippet that shows a literal asterisk.
     *
     * <p>Here is a snippet:
     * {@snippet :
     * int total = first *
     *     second;
     * }
     *
     * @return Nothing
     */
    public int second() {
        return this.number;
    }
}
