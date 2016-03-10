/**
 * Hello.
 */
package foo.bar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Better to use diamond operator where possible.
 *
 * @author John Smith (john@example.com)
 * @version $Id$
 * @since 1.0
 */
public final class InvalidDiamondsUsage {

    /**
     * Ten.
     */
    public static final int TEN = 10;

    @Override
    public List<String> firstTen(final String... args) {
        final List<String> list = new ArrayList<String>(args.length);
        list.addAll(Arrays.asList(args));
        final List<String> result = new LinkedList<>();
        result.addAll(list.subList(0, InvalidDiamondsUsage.TEN));
        return result;
    }
}
