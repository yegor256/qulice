/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class ValidSnapshot {
    private static int current = 1;
    private static final int INITIAL = ValidSnapshot.current;

    public static int read() {
        ValidSnapshot.current = 2;
        return ValidSnapshot.INITIAL;
    }
}
