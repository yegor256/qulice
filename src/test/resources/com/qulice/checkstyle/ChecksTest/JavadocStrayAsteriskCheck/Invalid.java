/*
 * Hello.
 */
package com.qulice.checkstyle;

/**
 * This is not a real Java class. It won't be compiled ever.
 *
 * <p>The paragraph below is closed with a bare asterisk, instead of
 * being closed with a tag or with nothing at all. *
 */
public final class Invalid {

    /**
     * A field. *
     */
    private int number;

    /**
     * A method.
     *
     * <p>Here the asterisk sits on its own line.
     * *
     *
     * @return Nothing
     */
    public int method() {
        return this.number;
    }

    /**
     * An annotated method. *
     *
     * @return Nothing
     */
    @SuppressWarnings("unchecked")
    public int annotated() {
        return this.number;
    }
}
