/*
 * Hello.
 */
package foo;

/**
 * Simple.
 * @since 1.0
 */
public final class ValidEqualityOperatorWrap {

    /**
     * Value.
     */
    private final int value;

    /**
     * Ctor.
     * @param val Value
     */
    public ValidEqualityOperatorWrap(final int val) {
        this.value = val;
    }

    /**
     * Returns something.
     * @param other Other
     * @return True if matches
     */
    public boolean matches(final int other) {
        return this.value
            == other;
    }
}
