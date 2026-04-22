/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
package com.qulice.checkstyle;

/**
 * Invalid usages of the @throws tag.
 * @since 1.0
 */
public final class Invalid {

    /**
     * Header can print itself to string.
     * @throws Exception If something goes wrong.
     */
    public void noSignature() {
    }

    /**
     * Header can print itself.
     * @throws IOException If something goes wrong.
     */
    public void mismatch() throws Exception {
    }

    /**
     * One matches, one does not.
     * @throws IOException When I/O fails.
     * @throws SQLException When database fails.
     */
    public void partial() throws IOException {
    }

    /**
     * Constructor.
     * @throws Exception Never.
     */
    public Invalid() {
    }
}
