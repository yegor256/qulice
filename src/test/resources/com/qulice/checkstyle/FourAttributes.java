/*
 * Hello.
 */
package foo;

/**
 * Class with four attributes.
 * @since 1.0
 */
public final class FourAttributes {

    /**
     * First number.
     */
    private final int first;

    /**
     * Second number.
     */
    private final int second;

    /**
     * Third number.
     */
    private final int third;

    /**
     * Fourth number.
     */
    private final int fourth;

    /**
     * Constructor.
     * @param one First number
     * @param two Second number
     * @param three Third number
     * @param four Fourth number
     */
    public FourAttributes(final int one, final int two, final int three, final int four) {
        this.first = one;
        this.second = two;
        this.third = three;
        this.fourth = four;
    }

    /**
     * Sum of all numbers.
     * @return The sum
     */
    public int sum() {
        return this.first + this.second + this.third + this.fourth;
    }
}
