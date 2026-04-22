/*
 * Hello.
 */
package foo;

import com.google.common.base.Charsets;
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
        System.out.println(this + Charsets.UTF_8);
        System.out.println(org.apache.commons.lang3.CharEncoding.UTF_8);
        System.out.println(org.apache.commons.lang.CharEncoding.ISO_8859_1);
        System.out.println(org.apache.commons.codec.CharEncoding.UTF_16);
        System.out.println(com.google.common.base.Charsets.UTF_8);
    }
}
