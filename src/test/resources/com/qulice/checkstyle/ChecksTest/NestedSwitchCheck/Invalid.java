/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public class Invalid {
    public int next(final int xxx, final int yyy) {
        final int result;
        switch (xxx) {
            case 0:
                switch (yyy) {
                    case 0:
                        result = 0;
                        break;
                    default:
                        result = 1;
                }
                break;
            default:
                result = 2;
        }
        return result;
    }
}
