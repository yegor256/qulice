/**
 * Hello.
 */
package foo.bar;

import java.util.ArrayList;
import java.util.List;

/**
 * Better to use diamond operator where possible.
 *
 * @author John Smith (john@example.com)
 * @version $Id$
 * @since 1.0
 */
public final class ValidDiamondsUsage {

    /**
     * Ten.
     */
    public static final int TEN = 10;

    @Override
    public List<String> firstTen(final String... args) {
        final List<String> list = new ArrayList<>(args.length + 1);
        list.add("First element");
        return list;
    }
}
