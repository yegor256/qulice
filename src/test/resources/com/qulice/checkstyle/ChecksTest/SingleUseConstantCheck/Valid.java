/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    private static final long serialVersionUID = 1L;
    private static final String GREETING = "Hello, world!";
    private static final Pattern SPACE = Pattern.compile(" ");
    private static final int[] SIZES = {1, 2, 3};
    private static final Valid INSTANCE = new Valid();
    private static final String UNUSED = "never mentioned again";
    private static final String TWICE = "x";
    protected static final String PROTECTED = "y";
    private final String instance = "z";
    private static String mutable = "w";
    public void print() {
        System.out.println(Valid.GREETING);
        System.out.println(Valid.GREETING.length());
    }
    public String trim(String text) {
        return SPACE.matcher(text).replaceAll("");
    }
    public int size() {
        return SIZES[0];
    }
    public Valid self() {
        return Valid.INSTANCE;
    }
    public String twice() {
        return TWICE + Valid.TWICE;
    }
    public String others() {
        return Valid.PROTECTED + this.instance + Valid.mutable;
    }
    /**
     * Mentioned once by the code, and once by {@value #DOCUMENTED}.
     */
    private static final int DOCUMENTED = 7;
    public int documented() {
        return DOCUMENTED;
    }
    private static final class Inner {
        private static final String NESTED = "nested";
        public String first() {
            return Inner.NESTED;
        }
        public String second() {
            return NESTED;
        }
    }
}
