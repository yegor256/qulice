/*
 * Empty javadoc line at the beginning.
 */
package com.qulice.checkstyle;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 *
 * Wrong order of parameters.
 * @param <V> - value.
 * @param <K> - key.
 */
public final class Invalid<K, V> {

    /**
     * A field.
     */
    private final String field;

    /**
     * Javadoc with parameters in different order than the constructor
     *  signature.
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

    /**
     * Javadoc with a different number of parameters than in the method
     *  signature.
     * @param eparam - param e.
     * @return Param.
     */
    public String method2(final String cparam, final String dparam) {
        return cparam + dparam;
    }

    /**
     * Javadoc without parameters for a method with several parameters.
     * @return Param.
     */
    public String method3(final String hparam) {
        return hparam;
    }

    /**
     * Javadoc without generic parameter at the end.
     * @param list
     * @return Element.
     */
    public <T> T first(final List<T> list) {
        return list.get(0);
    }

    /**
     * Javadoc with parameters in different order than the method signature.
     * @param <T> - type.
     * @param list - list.
     * @param index - index.
     * @return Element.
     */
    public <T> T get(final List<T> list, final int index) {
        return list.get(index);
    }

    /**
     * Javadoc without generic parameters.
     */
    public final static class InnerEntry<K, V> {

    }
}
