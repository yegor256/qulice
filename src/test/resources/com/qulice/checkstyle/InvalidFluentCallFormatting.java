/*
 * Hello.
 */
package foo;

import java.util.stream.Stream;

/**
 * Simple.
 * @since 1.0
 */
public final class InvalidFluentCallFormatting {
    /**
     * Returns something.
     * @return Something
     */
    public String make() {
        return Stream.of("a", "b", "c")
            .map(String::toUpperCase)
            .collect(
                java.util.stream.Collectors.joining(",")
            );
    }
}
