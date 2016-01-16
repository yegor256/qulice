/**
 * Some text.
 * And other.
 */
package com.qulice.foo;

import java.io.InputStream;
import org.apache.commons.io.IOUtils;

/**
 * Test class.
 * @author John Smith (John.Smith@example.com)
 * @version $Id$
 * @since 1.0
 */
public final class Sample {
    /**
     * Utility constructor.
     */
    private Sample() {
        // do nothing
    }

    /**
     * Test method.
     * @return Stream.
     */
    public static InputStream test() {
        return IOUtils.toInputStream("oops");
    }
}
