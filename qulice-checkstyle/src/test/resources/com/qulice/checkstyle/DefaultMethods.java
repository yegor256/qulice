/*
 * Hello.
 */
package foo;

import java.util.List;
import java.util.function.Consumer;

/**
 * Simple.
 * @since 1.0
 */
public interface DefaultMethods {
    /**
     * Some data.
     * @return Some data.
     */
    List<String> data();

    /**
     * Some default method.
     * @param action Value to print
     */
    default void forEach(final Consumer<String> action) {
        for (final String blah : this.data()) {
            action.accept(blah);
        }
    }
}

