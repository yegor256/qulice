/*
 * Empty javadoc line at the beginning.
 */
package com.qulice.checkstyle;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {

    /**
     * A field.
     */
    private final String field;

    /**
     * Javadoc with parameters in same order than the constructor signature.
     * @param bparam - param b.
     * @param aparam - param a.
     */
    Valid(final String bparam, final String aparam) {
        this.field = bparam.concat(aparam);
    }

    /**
     * Javadoc with parameters in same order than the method signature.
     * @param bparam - param b.
     * @param aparam - param a.
     * @return Param.
     */
    public String method(final String bparam, final String aparam) {
        return bparam + aparam;
    }

    /**
     * Correct order of parameters.
     * @param list List.
     * @param index Index.
     * @param <T> Type.
     * @return Element.
     */
    public <T> T get(final List<T> list, final int index) {
        return list.get(index);
    }

    /**
     * Correct order of parameters.
     * @param <K> Key.
     * @param <V> Value.
     */
    public final static class Entry<K, V> {

    }
}
