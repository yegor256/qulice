/*
 * Hello.
 */
package foo;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Sample class for testing ConditionalRegexpMultilineCheck suppression.
 *
 * @since 1.0
 */
public final class SuppressConditionalRegexpMultilineInComment {
    /**
     * Size of the container.
     */
    private final int size;

    /**
     * Primary constructor.
     * @param sze Size to store.
     */
    public SuppressConditionalRegexpMultilineInComment(final int sze) {
        this.size = sze;
    }

    /**
     * Build a collection without size.
     * @return An empty collection sized to this object.
     * @checkstyle ConditionalRegexpMultilineCheck (5 lines)
     */
    public Collection<String> build() {
        final Collection<String> list = new ArrayList<>();
        for (int idx = 0; idx < this.size; ++idx) {
            list.add(String.valueOf(idx));
        }
        return list;
    }
}
