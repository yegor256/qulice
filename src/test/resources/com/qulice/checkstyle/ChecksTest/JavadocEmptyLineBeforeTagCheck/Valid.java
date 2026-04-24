/*
 * All Javadocs below follow the rule: single-paragraph body has no empty
 * line before at-clauses; multi-paragraph body requires one.
 */
package com.qulice.checkstyle;

/**
 * Single-paragraph class Javadoc.
 * @since 0.27.0
 */
public final class Valid {

    /**
     * Single-paragraph field Javadoc.
     * @since 0.27.0
     */
    private static final int X = 0;

    /**
     * Single-paragraph ctor Javadoc.
     * @param value Any value
     */
    public Valid(final int value) {
    }

    /**
     * Single-paragraph method Javadoc.
     * @param param Some value
     * @return The same value
     */
    public String method(final String param) {
        return param;
    }

    /**
     * First paragraph of the Javadoc.
     *
     * <p>Second paragraph of the Javadoc.
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
     * <p>Second par.
     */
    public void noTagsMultiple() {
    }
}
