/*
 * Empty javadoc line at the beginning.
 */
package com.qulice.checkstyle;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {

    /**
     * Javadoc with parameters in same order than the method signature.
     * @param bparam - param b.
     * @param aparam - param a.
     * @return Param.
     */
    public String method(final String bparam, final String aparam) {
        return bparam + aparam;
    }
}
