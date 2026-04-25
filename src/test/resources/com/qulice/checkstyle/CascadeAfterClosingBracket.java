/*
 * Hello.
 */
package foo;

import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * Simple.
 * @since 1.0
 */
public final class CascadeAfterClosingBracket {

    /**
     * The reducer.
     */
    private final BinaryOperator<String> reducer;

    /**
     * The items.
     */
    private final List<String> items;

    /**
     * Ctor.
     * @param red Reducer
     * @param list Items
     */
    public CascadeAfterClosingBracket(final BinaryOperator<String> red,
        final List<String> list) {
        this.reducer = red;
        this.items = list;
    }

    /**
     * Take it.
     * @return The value
     * @throws Exception If fails
     */
    public Object take() throws Exception {
        return new CascadeAfterClosingBracket(
            (first, last) -> first,
            Collections.emptyList()
        )
            .reducer;
    }
}
