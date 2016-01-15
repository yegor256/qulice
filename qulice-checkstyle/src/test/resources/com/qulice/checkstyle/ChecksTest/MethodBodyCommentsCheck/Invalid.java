/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    /**
     * Comments.
     */
    public Invalid(String name, String value) {
        /* Comments */
        int i;
        // Comments
    }
    public void print(String format, String text) {
        // Comments
        int c = 0;
        /* Comments */
    }

    public int invalidCommentInside() {
        final int first = 1;
/* invalid comment */ final int second = 2;
        return first + second;
    }

    public int invalidMultilineInside() {
        /**
         * invalid multiline comment
         */
    }

    void submit() {
        new Runnable() {
            @Override
            public void run() {
                /* run
                 */
            }
        };
    }
}

