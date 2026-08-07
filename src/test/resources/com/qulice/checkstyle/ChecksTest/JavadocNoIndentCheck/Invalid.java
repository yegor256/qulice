/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in ChecksTest.
 */
package com.qulice.checkstyle;

/**
 * This is a paragraph.
 *     This line is over-indented and must be reported.
 */
public final class Invalid {

    /**
     * First sentence.
     *   Over-indented body line.
     */
    private static final int X = 0;

    /**
     * Some list of items.
     *
     * <ul>
     *  <li>Over-indented list item.
     * </ul>
     */
    private int field = 0;

    /**
     * Some text before a snippet.
     *  Over-indented body line after the summary.
     */
    private int other = 0;

    /**
     * A method with a tag.
     *   Over-indented line before the tag.
     * @param arg Some argument that wraps onto the
     *  next line with one extra space, which is fine
     * @return Nothing at all
     */
    public String method(final String arg) {
        return arg;
    }
}
