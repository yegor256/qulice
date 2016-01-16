/**
 * Hello.
 */
package foo;

/**
 * Simple.
 * @version $Id$
 * @author John Smith (john@example.com)
 * @checkstyle HiddenField (100 lines)
 */
public final class LocalVariableNames {
    /**
     * Just a field.
     */
    private transient int field;
    /**
     * Just an id.
     */
    private transient int id;

    /**
     * Names that should not cause any violation.
     */
    void valid() {
        try {
            int aaa = this.field;
            int id = ++aaa;
            final int ise = 0;
            final int twelveletter = ++aaa + ++id;
        } catch (final IllegalStateException ise) {
            throw ise;
        } catch (final IllegalArgumentException ex) {
            throw ex;
        }
    }

    /**
     * Each of those seven names here should cause violation.
     */
    void invalid() {
        try {
            int prolongations = 0;
            int very_long_variable_id = 0;
            int camelCase = this.field;
            int it = 0;
            final int number1 = ++prolongations;
            final int ex = ++camelCase;
            final int a = ++it + ++very_long_variable_id;
        } catch (final ArithmeticException ae) {
            throw ae;
        } catch (final IllegalArgumentException e) {
            throw e;
        }
    }
}

