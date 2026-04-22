/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
package com.qulice.checkstyle;

/**
 * Valid usages of the @throws tag.
 * @since 1.0
 */
public final class Valid {

    /**
     * Header can print itself.
     * @throws Exception If something goes wrong.
     */
    public void matches() throws Exception {
    }

    /**
     * Fully qualified form in javadoc matches unqualified signature.
     * @throws java.io.IOException When I/O fails.
     */
    public void qualified() throws IOException {
    }

    /**
     * No @throws tag at all.
     */
    public void silent() throws Exception {
    }

    /**
     * No throws anywhere.
     */
    public void plain() {
    }

    /**
     * Constructor with matching @throws.
     * @throws IOException On failure.
     */
    public Valid() throws IOException {
    }
}
