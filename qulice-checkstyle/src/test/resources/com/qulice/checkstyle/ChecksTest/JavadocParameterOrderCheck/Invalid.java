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
     * A field.
     */
    private final String field;

    /**
     * Javadoc with parameters in same order than the constructor signature.
     * @param bparam - param b.
     * @param aparam - param a.
     */
    Invalid(final String aparam, final String bparam) {
        this.field = bparam.concat(aparam);
    }

    /**
     * Javadoc with parameters in different order than the method signature.
     * @param bparam - param b.
     * @param aparam - param a.
     * @return Param.
     */
    public String method(final String aparam, final String bparam) {
        return bparam + aparam;
    }
}
