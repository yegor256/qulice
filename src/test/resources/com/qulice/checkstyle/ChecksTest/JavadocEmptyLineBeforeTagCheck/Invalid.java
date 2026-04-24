/*
 * Javadocs below violate the rule about empty line before at-clauses.
 */
package com.qulice.checkstyle;

/**
 * Single-paragraph class Javadoc.
 *
 * @since 0.27.0
 */
public final class Invalid {

    /**
     * Single-paragraph field Javadoc.
     *
     * @since 0.27.0
     */
    private static final int X = 0;

    /**
     * First paragraph of the Javadoc.
     *
     * <p>Second paragraph of the Javadoc.
     * @since 0.27.0
     */
    public Invalid() {
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
     * <p>Second paragraph.
     * @param param Some value
     * @return The same value
     */
    public String another(final String param) {
        return param;
    }
}
