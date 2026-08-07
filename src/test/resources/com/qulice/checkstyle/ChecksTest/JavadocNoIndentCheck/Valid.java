/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in ChecksTest.
 */
package com.qulice.checkstyle;

/**
 * This is a paragraph.
 * This line has exactly one space of indentation.
 */
public final class Valid {

    /**
     * A code sample that must keep its indentation.
     *
     * <pre>
     * if (x) {
     *     y();
     *         deeper();
     * }
     * </pre>
     */
    private static final int X = 0;

    /**
     * A snippet that must keep its indentation.
     *
     * {@snippet :
     *     System.out.println("hello");
     *         System.out.println("world");
     * }
     */
    private int field = 0;

    /**
     * A method with wrapped tags.
     * @param arg Some argument description that is long and
     *  wraps onto the next line with one extra space
     * @return Some value that also has a description wrapping
     *  onto a second line, indented by one extra space
     */
    public String method(final String arg) {
        return arg;
    }

    /**
     * An inline snippet {@snippet foo} on a single line.
     * Some more text right after it.
     */
    public void second() {
        // nothing
    }
}
