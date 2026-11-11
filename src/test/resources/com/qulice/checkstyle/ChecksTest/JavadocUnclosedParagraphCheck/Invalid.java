/*
 * Hello.
 */
package com.qulice.checkstyle;

/**
 * This is not a real Java class. It won't be compiled ever.
 *
 * <p>The first paragraph is still open when the second one starts.
 *
 * <p>And the second one is still open when the comment ends.
 */
public final class Invalid {

    /**
     * A field.
     *
     * <p>A single paragraph that nobody ever closes.
     */
    private int number;

    /**
     * A method.
     *
     * <p>This one is closed properly.</p>
     *
     * <p>This one is not, and the block tags below do not close it.
     *
     * @return Nothing
     */
    public int method() {
        return this.number;
    }

    /**
     * An annotated method.
     *
     * <p>Left open, behind an annotation.
     *
     * @return Nothing
     */
    @SuppressWarnings("unchecked")
    public int annotated() {
        return this.number;
    }

    /**
     * A method whose inline code must not count as a tag.
     *
     * <p>The text mentions {@code <p>} and {@code </p>}, yet the paragraph
     * itself is never closed.
     *
     * @return Nothing
     */
    public int inline() {
        return this.number;
    }
}
