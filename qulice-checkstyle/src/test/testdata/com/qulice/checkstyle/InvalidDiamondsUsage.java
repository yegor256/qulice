/*
 * Hello.
 */
package foo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Better to use diamond operator where possible.
 *
 * @since 1.0
 */
public final class InvalidDiamondsUsage {

    @Override
    public List<String> firstTen(final String... args) {
        final List<String> list = new ArrayList<String>(args.length);
        list.addAll(Arrays.asList(args));
        return list;
    }

    /**
     * Wrong diamonds usage when instantiate an inner class type.
     */
    public static void innerClassUsage() {
        final SimpleInterface.InnerClass<String> inner =
            new SimpleInterface.InnerClass<String>();
    }

    /**
     * Simple interface, used as wrapper.
     * @since 1.0.0
     */
    interface SimpleInterface {

        /**
         * Inner class with generic parameter.
         * @param <E> generic parameter
         * @since 1.0.0
         */
        final class InnerClass<E> implements SimpleInterface {

        }
    }
}
