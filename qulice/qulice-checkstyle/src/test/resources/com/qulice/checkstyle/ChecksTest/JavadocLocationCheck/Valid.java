/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {

    /**
     * Integer variable.
     */
    private int x = 0;

    /**
     * Public ctor.
     * @param num Some number
     */
    @SuppressWarnings("test")
    public Valid(final Integer num) {}

    /**
     * Some other method.
     * @param alpha Some number
     * @param beta Some string
     */
    @SuppressWarnings("test")
    public byte[] method(final int[] alpha, final String beta) {
        int a = 5;
        int b = 3;
        final String texts = new String[] {"hello there!"};
        assert a != b;
        final Runnable runnable = new Runnable() {
            @Override
            void run() {
                Valid.this.send(new String[] {"oops"});
            }
        };
        return new byte[] {1};
    }
}
/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
interface I1 {
    /**
     * Integer variable.
     */
    private int x = 0;
}
