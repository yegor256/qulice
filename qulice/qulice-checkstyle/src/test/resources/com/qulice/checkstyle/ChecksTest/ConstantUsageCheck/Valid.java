/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    private static final Invalid INSTANCE = new Invalid();
    private static final int NUMBER = 5;
    protected String NAME = "DDD";
    protected static final int N = NUMBER*2;
    private static int K = 7;
    static {
        System.out.println(INSTANCE.toString());
    }
    {
        System.out.println("Before start");
    }
    public int do1(int y) {
        print(INSTANCE);
        final Integer x = 5;
        return y * NUMBER * x;
    }
}
