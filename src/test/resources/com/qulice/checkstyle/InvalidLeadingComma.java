/*
 * Hello.
 */
package foo;

/**
 * Demonstrates a comma placed at the beginning of a line, which is
 * disallowed by the Google Java Style Guide.
 * @since 1.0
 */
public final class InvalidLeadingComma {

    /**
     * Format a name into a greeting.
     * @param name The name
     * @return The formatted text
     */
    public String greet(final String name) {
        return String.format(
            "My name is %s"
            , name
        );
    }
}
