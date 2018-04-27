/*
 * Hello.
 */
package foo;

import java.util.List;

/**
 * Simple.
 * @author John Smith (john@example.com)
 * @version $Id$
 * @since 1.0
 */
public interface ParametrizedClass<T> {
    /**
     * Some data.
     * @return Some data.
     */
    List<T> data();

}

