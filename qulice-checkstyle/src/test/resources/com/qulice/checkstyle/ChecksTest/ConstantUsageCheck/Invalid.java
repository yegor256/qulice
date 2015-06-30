/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    private static final Invalid INSTANCE = new Invalid();
    private static final int[] OTHER_INSTANCE = { 1, 2, 3, 4 };
    private final int NUMBER = 5;
    protected String NAME = "DDD";
    private static final int N = NUMBER*2;
    static {
        System.out.println(N);
    }
    {
        System.out.println("Before start");
    }
    public int do1(int y) {
        return y * y;
    }
}
