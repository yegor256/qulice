/**
 * Hello.
 */
package foo;

import java.util.List;

/**
 * Simple.
 * @version $Id$
 * @author John Smith (john@example.com)
 * @since 1.0
 */
public interface ParametrizedClass<T> {
    /**
     * Some data.
     * @return Some data.
     */
    List<T> data();

}

