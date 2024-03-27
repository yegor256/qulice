/*
 * Hello.
 */
package foo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;

/**
 * Better to use diamond operator where possible.
 *
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
     * Correct diamonds usage when instantiate an inner class type.
     */
    public static void innerClassUsage() {
        final SimpleInterface.InnerClass<String> inner =
            new SimpleInterface.InnerClass<>();
    }

    /**
     * Correct non-diamonds, types required for inner entity.
     */
    public static void foo() {
        final Map<String, String> params = new MapOf<String, String>(
            new MapEntry<>("a", "foo"),
            new MapEntry<>("b", "foo")
        );
    }

    /**
     * Simple interface, used as wrapper.
     * @since 1.0
     */
    interface SimpleInterface {

        /**
         * Inner class with generic parameter.
         * @param <E> generic parameter
         * @since 1.0
         */
        final class InnerClass<E> implements SimpleInterface {

        }
    }
}
