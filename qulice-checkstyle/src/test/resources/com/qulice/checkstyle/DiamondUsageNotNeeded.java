/*
 * Hello.
 */
package foo.bar;

import java.util.ArrayList;
import java.util.List;

/**
 * Some comment.
 *
 * @since 1.0
 */
public final class DiamondUsageNotNeeded {

    @Override
    public List<String> firstTen(final String... args) {
        return new ArrayList<String>(2);
    }
}
