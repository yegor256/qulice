/*
 * Hello.
 */
package foo;

/**
 * Correct Javadoc for class {@link AtClauseOrder}.
 * @since 1.0
 */
public final class ValidSingleLineCommentCheck {

    /**
     * A valid literal (Qulice may not report its contents as it is domain-specific string,
     * not Java code).
     */
    public static final String LITERAL_WHICH_LOOKS_LIKE_COMMENT = "/* Hello */";

    /**
     * Same here.
     */
    public static final String ANOTHER_LITERAL = "/**/";

    /** Valid single line literal. */
    public static final String SINGLE_LINE_LITERAL = "/**   this is not comment  **/";

    /**
     * A literal with a Javadoc-like prefix followed by more code on the same line.
     * See https://github.com/yegor256/qulice/issues/975.
     */
    public static final String CODE_WITH_COMMENT_LIKE_PREFIX =
        "/** A is a simple class */ class A { }";

    /**
     * Valid multi line literal.
     */
    public static final String[] MULTILINE_LITERAL = {
        " /**", " * @since 0.3.4.4.", " **/",
        " /**",
        " * @since 0.3.4.4.",
        " **/",
    };

    /**
     * Empty constructor.
     */
    private AtClauseOrder() {
    }

    /**
     * Javadoc-like literal inside a method body.
     * See https://github.com/yegor256/qulice/issues/975.
     */
    public static void methodWithCommentLikeLiteral() {
        final var code = "/** A is a simple class */ class A { }";
        System.out.println(code);
    }
}
