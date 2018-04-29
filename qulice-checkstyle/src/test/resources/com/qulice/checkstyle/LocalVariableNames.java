/*
 * Hello.
 */
package foo;

/**
 * Simple.
 * @since 1.0
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
     * Just a valid method.
     *
     * @param id
     *  A valid parameter with name 'id'.
     * @return Some value
     */
    static int validone(final int id) {
        return id + 1;
    }

    /**
     * Another valid method.
     * @param parametername Another parameter that's valid.
     */
    static void validtwo(final int parametername) {
        if (parametername == 1) {
            throw new RuntimeException("Some error");
        }
    }

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

    /**
     * Just an invalid method that test all cases.
     *
     * @param it
     *  An invalid parameter with name 'it'.
     * @return Some value
     */
    static int invalid(final int it) {
        return it + 1;
    }
}
