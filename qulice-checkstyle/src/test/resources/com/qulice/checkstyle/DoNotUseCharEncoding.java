/*
 * Hello.
 */
package foo;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang3.CharEncoding;

/**
 * Simple.
 * @since 1.0
 */
public final class DoNotUseCharEncoding {
    /**
     * Act.
     */
    public void act() {
        System.out.println(this + CharEncoding.UTF_8);
        System.out.println(org.apache.commons.lang3.CharEncoding.UTF_8);
        System.out.println(org.apache.commons.lang.CharEncoding.ISO_8859_1);
        System.out.println(org.apache.commons.codec.CharEncoding.UTF_16);
    }
}
