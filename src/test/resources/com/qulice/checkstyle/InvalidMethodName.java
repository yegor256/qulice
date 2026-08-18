/*
 * Hello.
 */
package foo;

/**
 * Rejects a two-letter name that is not an English word.
 * @since 1.0
 */
public final class InvalidMethodName {

    /**
     * The text.
     */
    private final String text;

    /**
     * Ctor.
     * @param txt The text
     */
    public InvalidMethodName(final String txt) {
        this.text = txt;
    }

    /**
     * The text.
     * @return The text
     */
    public String zz() {
        return this.text;
    }
}
