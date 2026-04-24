/*
 * Hello.
 */
package foo;

import java.util.List;

/**
 * Simple.
 * @since 1.0
 */
public final class ValidEnhancedForColon {

    /**
     * Base value.
     */
    private final int base;

    /**
     * Ctor.
     * @param start Starting value
     */
    public ValidEnhancedForColon(final int start) {
        this.base = start;
    }

    /**
     * Sum with base.
     * @param numbers Numbers
     * @return Total
     */
    public int sum(final List<Integer> numbers) {
        int total = this.base;
        for (final Integer number : numbers) {
            total += number;
        }
        return total;
    }
}
