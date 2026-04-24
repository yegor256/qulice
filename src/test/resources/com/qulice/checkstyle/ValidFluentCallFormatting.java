/*
 * Hello.
 */
package foo;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple.
 * @since 1.0
 */
public final class ValidFluentCallFormatting {

    /**
     * Separator.
     */
    private final String sep;

    /**
     * Ctor.
     * @param sep Separator
     */
    public ValidFluentCallFormatting(final String sep) {
        this.sep = sep;
    }

    /**
     * Returns something.
     * @return Something
     */
    public String make() {
        return Stream.of("a", "b", "c")
            .map(String::toUpperCase).collect(
                Collectors.joining(this.sep)
            );
    }

    /**
     * Returns something else.
     * @return Something else
     */
    public String pick() {
        return Stream.of("a", "b", "c").filter(
            str -> !str.isEmpty() && !this.sep.isEmpty()
        ).findFirst().orElse("");
    }
}
