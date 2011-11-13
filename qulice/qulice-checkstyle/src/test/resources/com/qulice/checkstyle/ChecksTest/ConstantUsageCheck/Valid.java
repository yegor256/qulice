/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    private final int NUMBER = 5;
    protected String NAME = "DDD";
    static {
        System.out.println(NUMBER);
    }
    {
        System.out.println("Before start");
    }
    public int do1(int y) {
        final Integer x = 5;
        return y * NUMBER * x;
    }
}
