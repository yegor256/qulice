/*
 * Hello.
 */
package com.qulice.checkstyle;

/**
 * This is not a real Java class. It won't be compiled ever.
 *
 * <p>Every paragraph here is closed where it ends.</p>
 *
 * <p>Including this one, which spans more than a single line before
 * it finally closes.</p>
 */
public final class Valid {

    /**
     * A field, described without any paragraph at all.
     */
    private int number;

    /**
     * A method whose paragraph closes before the block tags.
     *
     * <p>Closed right here.</p>
     *
     * @return Nothing
     */
    public int first() {
        return this.number;
    }

    /**
     * A method that writes the tags as inline code.
     *
     * <p>A paragraph opens with {@code <p>} and closes with {@code </p>},
     * and neither of those counts as a real tag here.</p>
     *
     * @return Nothing
     */
    public int inline() {
        return this.number;
    }

    /**
     * A method with a pre block that shows literal tags.
     *
     * <p>Example configuration:</p>
     * <pre>
     * &lt;p&gt;
     * some example
     * </pre>
     *
     * @return Nothing
     */
    public int second() {
        return this.number;
    }

    /**
     * A method with a snippet that shows literal tags.
     *
     * <p>Here is a snippet:</p>
     * {@snippet :
     * <p>
     * text
     * }
     *
     * @return Nothing
     */
    public int third() {
        return this.number;
    }
}
