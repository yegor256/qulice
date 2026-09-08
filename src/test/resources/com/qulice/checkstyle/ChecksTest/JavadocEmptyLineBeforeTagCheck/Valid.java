/*
 * All Javadocs below follow the rule: the block of at-clauses is always
 * preceded by an empty Javadoc line.
 */
package com.qulice.checkstyle;

/**
 * Single-paragraph class Javadoc.
 *
 * @since 0.27.0
 */
public final class Valid {

    /**
     * Single-paragraph field Javadoc.
     *
     * @since 0.27.0
     */
    private static final int X = 0;

    /**
     * Single-paragraph ctor Javadoc.
     *
     * @param value Any value
     */
    public Valid(final int value) {
    }

    /**
     * Single-paragraph method Javadoc.
     *
     * @param param Some value
     * @return The same value
     */
    public String method(final String param) {
        return param;
    }

    /**
     * First paragraph of the Javadoc.
     *
     * <p>Second paragraph of the Javadoc.</p>
     *
     * @param param Some value
     * @return The same value
     */
    public String another(final String param) {
        return param;
    }

    /**
     * Body without any at-clauses at all.
     */
    public void noTags() {
    }

    /**
     * Body with no tags but multiple paragraphs.
     *
     * <p>Second par.</p>
     */
    public void noTagsMultiple() {
    }

    /**
     * @param param Some value
     * @return The same value
     */
    public String noBody(final String param) {
        return param;
    }
}
