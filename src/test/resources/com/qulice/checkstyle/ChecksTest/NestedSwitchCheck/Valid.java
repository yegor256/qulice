/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public class Valid {
    public int outer(final int xxx) {
        final int result;
        switch (xxx) {
            case 0:
                result = 0;
                break;
            default:
                result = 1;
        }
        return result;
    }

    public int inner(final int yyy) {
        final int result;
        switch (yyy) {
            case 0:
                result = 0;
                break;
            default:
                result = 1;
        }
        return result;
    }
}
