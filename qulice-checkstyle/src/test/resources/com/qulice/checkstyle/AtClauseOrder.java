/*
 * Hello.
 */
package foo;

/**
 * Correct Javadoc for class {@link AtClauseOrder}.
 *
 * @see AtClauseOrder
 * @serial Serial
 * @serialField
 * @serialData
 * @since 1.0
 * @deprecated Reason
 */
@Deprecated
public final class AtClauseOrder {
    /**
     * Empty constructor.
     * @checkstyle Suppression
     * @todo Puzzle
     */
    private AtClauseOrder() {
    }

    /**
     * Just a method with valid Javadoc.
     *
     * @param input Valid parameter
     * @return Some value
     * @exception Exception
     * @throws  Exception
     * @see AtClauseOrder
     * @since 1.0
     * @deprecated Reason
     * @todo Puzzle
     * @checkstyle Suppression
     */
    @Deprecated
    public static int firstMethod(final int input) {
        return input;
    }

    /**
     * Just a method with invalid Javadoc.
     *
     * @see AtClauseOrder
     * @return Some value
     */
    public static int secondMethod() {
        return input;
    }

    /**
     * Just a class with inavlid Javadoc.
     *
     * @since 1.0
     */
    private class Class {
    }
}
