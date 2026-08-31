/*
 * Hello.
 */
package foo;

/**
 * Classname contains no abbreviations.
 * Contains overridden method for which otherwise invalid uppercase use is
 * allowed.
 * @since 1.0
 */
public class ValidAbbreviationAsWordInName extends SomeClass {

    /**
     * Example final static is valid and does not need to be in camelcase.
     */
    private static final String CONST_VALUE = "foo";

    /**
     * Ctor.
     */
    public ValidAbbreviationAsWordInName() {
        // nothing
    }

    /**
     * The constant, so that it is not used only once.
     * @return The constant
     */
    public static String constant() {
        return ValidAbbreviationAsWordInName.CONST_VALUE;
    }

    @Override
    public final String UPPERCASE() {
        return ValidAbbreviationAsWordInName.CONST_VALUE;
    }

    /**
     * ValidInnerHtml example class having the abbreviation in camelcase.
     * @since 1.0
     */
    public class ValidInnerHtml {

        /**
         * Ctor.
         */
        public ValidInnerHtml() {
            // nothing
        }
    }
}
