/*
 * Hello.
 */
package foo;

/**
 * Simple.
 * @since 1.0
 */
public final class ConstructorParams {
    /**
     * Some number.
     */
    private final int number;

    /**
     * Constructor.
     * @param number Some nice number.
     */
    public ConstructorParams(final int number) {
        this.number = number;
    }

    /**
     * Add an external number to internal one.
     * @param number Number to add.
     * @return Sum of numbers.
     */
    public int addNumber(final int number) {
        return this.number + number;
    }
}

