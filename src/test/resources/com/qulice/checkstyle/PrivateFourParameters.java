/*
 * Hello.
 */
package foo;

/**
 * Class with a private method of four parameters.
 * @since 1.0
 */
public final class PrivateFourParameters {

    /**
     * The base.
     */
    private final int base;

    /**
     * Ctor.
     * @param num The base
     */
    public PrivateFourParameters(final int num) {
        this.base = num;
    }

    /**
     * Sum of a few numbers.
     * @return The sum
     */
    public int total() {
        return this.sum(1, 2, 3, 4);
    }

    /**
     * Sum of all numbers and the base.
     * @param one First number
     * @param two Second number
     * @param three Third number
     * @param four Fourth number
     * @return The sum
     */
    private int sum(final int one, final int two, final int three, final int four) {
        return this.base + one + two + three + four;
    }
}
