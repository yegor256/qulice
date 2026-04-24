/*
 * Hello.
 */
package foo;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * Sample class for testing Guava Lists.newArrayList() detection.
 *
 * @since 1.0
 */
public final class GuavaNewArrayList {

    /**
     * Build a list using Guava helper without size.
     * @return An empty list.
     */
    public List<String> build() {
        return Lists.newArrayList();
    }
}
