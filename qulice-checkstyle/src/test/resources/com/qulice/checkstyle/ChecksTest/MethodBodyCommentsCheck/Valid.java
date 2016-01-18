/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    /**
     * Comments.
     */
    public Invalid(String name, String value) {
        this.a = b;
        // @checkstyle MagicNumber (1 line)
        this.a = b;
    }
    /**
     * Comments.
     */
    public void print(String format, String text) {
        int c = 0;
        // @checkstyle MagicNumber (1 line)
        this.a = b;
    }
    public void print() {
        // empty method, nothing here but a comment
    }
    /**
     * Some comment.
     */
    @Override
    public void print() {
        // empty
    }
    /**
     * Some method.
     */
    public void someMethod() {
        final Closeable resource = new Closeable() {
            @Override
            public void close() {
                // nothing to close here
            }
        };
    }
}

/**
 * Example with an interface.
 */
public interface ValidInterface {
    /**
     * Some method with no body
     */
    void method();

    /* Some plain comment outside method*/
    void method();
    /* Another plain comment outside method */
}
