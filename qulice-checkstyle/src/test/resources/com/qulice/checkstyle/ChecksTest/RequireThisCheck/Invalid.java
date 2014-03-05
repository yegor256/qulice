/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */

public final class Invalid {
    private final int number = 1;

    public int check() {
        int sum = number;
        sum += other();
        return sum;
    }

    private int other() {
        return 0;
    }
}
