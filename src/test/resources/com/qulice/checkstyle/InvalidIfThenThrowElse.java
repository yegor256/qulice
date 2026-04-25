/*
 * Hello.
 */
package foo;

/**
 * Samples with an else clause after a throwing then branch.
 * @since 1.0
 */
public final class InvalidIfThenThrowElse {

    /**
     * Label to print.
     */
    private final String label;

    /**
     * Constructor.
     * @param name The label
     */
    public InvalidIfThenThrowElse(final String name) {
        this.label = name;
    }

    /**
     * Braced then-throw with else block.
     * @param num A number
     */
    public void braced(final int num) {
        if (num < 0) {
            throw new IllegalArgumentException(this.label);
        } else {
            System.out.println(num);
        }
    }

    /**
     * Unbraced then-throw with else block.
     * @param num A number
     */
    public void unbraced(final int num) {
        if (num < 0) {
            throw new IllegalArgumentException(this.label);
        } else if (num == 0) {
            System.out.println("zero");
        }
    }

    /**
     * Both branches throw, still prohibited.
     * @param num A number
     */
    public void both(final int num) {
        if (num < 0) {
            throw new IllegalArgumentException(this.label);
        } else {
            throw new IllegalStateException("positive");
        }
    }
}
