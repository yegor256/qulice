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

    /**
     * Correct diamonds usage when instantiate an inner class type,
     * because there is no way that the diamond operator can be applied.
     */
    public static void innerClassUsage() {
        final SimpleInterface.InnerClass inner =
            new SimpleInterface.InnerClass();
    }

    /**
     * Simple interface, used as wrapper.
     */
    interface SimpleInterface {

        /**
         * Inner class.
         */
        final class InnerClass implements SimpleInterface {

        }
    }
}
