/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    private static final String GREETING = "Hello, world!";
    private static final int LIMIT = 42;
    private static final char SEPARATOR = ',';
    private static final String PREFIX = "a" + "b";
    private static final Charset ENCODING = StandardCharsets.UTF_8;
    public void print() {
        System.out.println(Invalid.GREETING);
    }
    public int limit(int y) {
        return y * LIMIT;
    }
    public String[] split(String text) {
        return text.split(String.valueOf(SEPARATOR));
    }
    public String name() {
        return PREFIX.trim();
    }
    public byte[] bytes(String text) {
        return text.getBytes(Invalid.ENCODING);
    }
}
