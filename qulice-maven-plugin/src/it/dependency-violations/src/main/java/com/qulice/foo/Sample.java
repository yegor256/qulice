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
 */
public final class Sample {
    /**
     * Test method.
     * @return Stream.
     */
    public InputStream test() {
        return IOUtils.toInputStream("oops");
    }
}
