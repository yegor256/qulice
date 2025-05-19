/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */

/**
 * Class comment.
 * @since 0.23.1
 */
public final class Valid {

    /**
     * Valid multiline comment
     */
    private static String VALID_CSTYLE_LITERAL = " /* C-style comment */";

    /**
     *  Valid multiline comment starts with two spaces
     */
    private static String VALID_CSTYLE_LITERAL_WITH_TWO_SP = " /*  C-style comment */";

    /** Valid single-line comment. */
    private static String SINGLE_LINE_LITERAL =
        " /** first sentence in a comment should start with a capital letter */";

    /**  Valid single-line comment starts with two spaces. */
    private static String SINGLE_LINE_LITERAL_WITH_TWO_SP =
        " /**  first sentence in a comment should start with a capital letter */";

    /** Valid multiline comment
     */
    public void main() {
    }

    /**  Valid multiline comment starts with two spaces
     */
    public void mainTwo() {
    }

}
