/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    private static final Invalid INSTANCE = new Invalid();
    private static final int[] OTHER_INSTANCE = { 1, 2, 3, 4 };
    private static final int NUMBER = 5;
    private static final long serialVersionUID = 1L;
    private final transient OutputStream stream = new ByteArrayOutputStream();
    protected String NAME = "DDD";
    protected static final int N = NUMBER*2;
    private static int K = 7;
    private static final String NAME = "name";
    static {
        System.out.println(INSTANCE.toString());
    }
    {
        System.out.println("Before start");
    }
    public int do1(int y, String[] texts) {
        print(INSTANCE, CharEncoding.UTF_8);
        print(this.stream.toString());
        final Integer x = 5;
        return y * NUMBER * x * OTHER_INSTANCE[0];
    }
    private void extra() {
        new Runnable() {
            @Override
            public void run() {
                Valid.this.stream.write(NUMBER);
                System.out.println(CharEncoding.UTF_8);
            }
        };
    }
    private static final class ValidInner {
        private final String name;
        ValidInner() {
            this.name = Valid.NAME;
        }
        public String get() {
            return this.name;
        }
    }
}
