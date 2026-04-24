/*
 * Hello.
 */
package foo;

/**
 * Simple.
 * @since 1.0
 */
public final class ValidIndentation {

    /**
     * The name.
     */
    private final String name;

    /**
     * Ctor.
     * @param txt Name
     */
    public ValidIndentation(final String txt) {
        this.name = txt;
    }

    /**
     * Do something.
     * @return The size
     */
    public int doit() {
        return this.name.length() + Arrays.asList(
            new Integer(
                1
            )
        ).size();
    }
}
