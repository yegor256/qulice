/*
 * Hello.
 */
package foo;

import java.lang.reflect.Method;

/**
 * Simple.
 * @see java.lang.Object
 * @since 1.0
 */
public final class UnnecessaryJavaLang {

    /**
     * Name.
     */
    private final java.lang.String name;

    /**
     * Ctor.
     * @param txt Text
     * @throws java.lang.Exception If a problem occurs
     */
    public UnnecessaryJavaLang(final String txt) throws java.lang.Exception {
        this.name = txt;
    }

    /**
     * Act.
     * @return Size
     */
    public int act() {
        final Method method = null;
        return this.name.length() + System.identityHashCode(method);
    }
}
