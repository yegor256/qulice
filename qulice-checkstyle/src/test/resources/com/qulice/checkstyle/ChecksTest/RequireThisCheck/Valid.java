/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */

public final class Valid {
    private final int number = 1;

    public int check() {
        int sum = this.number;
        sum += this.other();
        return sum;
    }

    private int other() {
        return 0;
    }
}
