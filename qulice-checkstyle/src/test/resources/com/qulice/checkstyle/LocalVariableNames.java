/**
 * Hello.
 */
package foo;

/**
 * Simple.
 * @version $Id$
 * @author John Smith (john@example.com)
 */
public final class LocalVariableNames {
    /**
     * Just a field.
     */
    private transient int field;

    /**
     * Names that should not cause any violation.
     */
    void valid() {
        try {
            int aaa = this.field;
            final int twelveletter = ++aaa;
        } catch (final IllegalStateException ise) {
            throw ise;
        } catch (final IllegalArgumentException ex) {
            throw ex;
        }
    }

    /**
     * Each name here should cause one violation.
     */
    void invalid() {
        try {
            int prolongations = 0;
            int camelCase = this.field;
            final int number1 = ++prolongations;
            final int ex = ++camelCase;
            final int a = 0;
        } catch (final ArithmeticException ae) {
            throw ae;
        } catch (final IllegalArgumentException e) {
            throw e;
        }
    }
}

