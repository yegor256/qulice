/*
 * Hello.
 */
package foo;

/**
 * Simple.
 * @since 1.0
 */
public final class ValidLiteralComparisonCheck {
    /**
     * Some text.
     */
    private final String text;

    /**
     * Constructor.
     * @param txt Some text.
     */
    public ValidLiteralCheck(final String txt) {
        this.text = txt;
    }

    /**
     * Method using input.equals("contents")
     * instead of "contents".equals(input).
     */
    public void validStringComparison() {
        System.out.println(this.text.equals("contents"));
        System.out.println("contents".equals(this.text));
    }
}
