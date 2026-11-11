/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public class Valid {
    /**
     * Ctor.
     * @param num The number
     */
    private Valid(final int num) {
        // nothing to do
    }

    /**
     * Calculate the sum.
     * @param left Left operand
     * @param right Right operand
     * @return The sum
     */
    public int sum(final int left, final int right) {
        return this.diff(left, right);
    }

    private int diff(final int left, final int right) {
        return left - right;
    }

    private static int mul(final int left, final int right) {
        // A private static method needs no Javadoc.
        return left * right;
    }
}
