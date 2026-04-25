/*
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
    /**
     * Multiple anonymous classes in a single method body.
     */
    public void multipleAnonymous() {
        final Closeable first = new Closeable() {
            @Override
            public void close() {
                // first
            }
        };
        final Closeable second = new Closeable() {
            @Override
            public void close() {
                // second
            }
        };
    }
    /**
     * Anonymous class passed as a method argument.
     */
    public void anonymousAsArgument() {
        register(new Runnable() {
            @Override
            public void run() {
                // running
            }
        });
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
